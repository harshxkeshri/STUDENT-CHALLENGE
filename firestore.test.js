const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- UNAUTHENTICATED TESTS ---

test("Unauthenticated user: cannot read users, clubs, or events", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").get());
  await assertFails(unauthDb.collection("clubs").get());
  await assertFails(unauthDb.collection("events").get());
});

// --- USER PROFILE & ROLE IMMUTABILITY TESTS ---

test("Authenticated user: can create their own profile with role STUDENT", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Smith",
      email: "alice@campus.edu",
      role: "STUDENT",
      campus: "HI-Tech Campus",
      department: "Computer Science",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot create profile for another user ID", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      name: "Bob Fake",
      email: "bob@campus.edu",
      role: "STUDENT",
      campus: "HI-Tech Campus",
      department: "Computer Science",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: can update bio and department", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Smith",
      email: "alice@campus.edu",
      role: "STUDENT",
      campus: "HI-Tech Campus",
      department: "Computer Science",
      bio: "Original bio",
      createdAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      bio: "Updated bio for robotics club",
      department: "Data Science",
    })
  );
});

test("Authenticated user: CANNOT switch their role (e.g., STUDENT -> CAMPUS_MANAGER)", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice Smith",
      email: "alice@campus.edu",
      role: "STUDENT",
      campus: "HI-Tech Campus",
      department: "Computer Science",
      createdAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Attempting to change role MUST fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      role: "CAMPUS_MANAGER",
    })
  );
});

// --- CLUBS & EVENTS TESTS ---

test("Authenticated user: can create a club and read clubs", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("clubs").doc("tech_club").set({
      clubId: "tech_club",
      name: "Robotics & AI Guild",
      category: "Technology",
      campus: "HI-Tech Campus",
      leadCoordinator: "Alice Smith",
      facultyAdvisor: "Dr. Raman",
      memberCount: 25,
      createdAt: new Date(),
    })
  );

  await assertSucceeds(aliceDb.collection("clubs").get());
  await assertSucceeds(aliceDb.collection("clubs").doc("tech_club").get());
});

test("Authenticated user: can create an event and read events", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("events").doc("hackathon_2026").set({
      eventId: "hackathon_2026",
      title: "Inter-Campus Hackathon 2026",
      clubId: "tech_club",
      clubName: "Robotics & AI Guild",
      campus: "HI-Tech Campus",
      date: "2026-11-20",
      venue: "Auditorium Hall A",
      capacity: 150,
      registeredCount: 0,
      organizerId: ALICE_UID,
      status: "UPCOMING",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(aliceDb.collection("events").get());
});

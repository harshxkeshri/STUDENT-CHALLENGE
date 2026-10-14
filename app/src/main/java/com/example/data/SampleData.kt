package com.example.data

import com.example.model.*

object SampleData {

    val defaultUser = UserProfile(
        id = "std_2026_042",
        name = "Chirag Sharma",
        email = "chirushi07@gmail.com",
        rollNo = "MSOT-2023-CS-042",
        campus = Campus.HI_TECH,
        department = "Computer Science & Engineering",
        academicYear = "3rd Year (Semester 6)",
        bio = "Full-stack developer & tech enthusiast. Active contributor in Kaizen Tech and Nexcell startup cohorts.",
        avatarColorHex = 0xFF4338CA,
        coCurricularCredits = 24,
        rankTitle = "Lead Innovation Fellow",
        role = UserRole.STUDENT,
        password = "password123"
    )

    val demoUsers = listOf(
        defaultUser,
        UserProfile(
            id = "coord_aryan",
            name = "Aryan Sharma",
            email = "aryan.sharma@msot.edu",
            rollNo = "MSOT-2023-CS-008",
            campus = Campus.HI_TECH,
            department = "Computer Science & Engineering",
            academicYear = "3rd Year (Semester 6)",
            bio = "Lead Coordinator of Kaizen Tech Club. Dedicated to open-source systems and competitive coding.",
            avatarColorHex = 0xFF0284C7,
            coCurricularCredits = 36,
            rankTitle = "Chief Student Coordinator",
            role = UserRole.COORDINATOR,
            password = "password123"
        ),
        UserProfile(
            id = "adv_ramanujan",
            name = "Dr. R. Ramanujan",
            email = "ramanujan@msot.edu",
            rollNo = "FAC-CS-104",
            campus = Campus.HI_TECH,
            department = "Computer Science & Engineering",
            academicYear = "Faculty Member",
            bio = "Professor & Head of Department. Faculty Advisor for Kaizen Tech Club.",
            avatarColorHex = 0xFF4338CA,
            coCurricularCredits = 50,
            rankTitle = "Faculty Advisor",
            role = UserRole.ADVISOR,
            password = "password123"
        ),
        UserProfile(
            id = "peer_vikram",
            name = "Vikram Patel",
            email = "vikram.patel@msot.edu",
            rollNo = "MSOT-2024-EC-021",
            campus = Campus.RATHINUM,
            department = "Electronics & Communication",
            academicYear = "2nd Year (Semester 4)",
            bio = "IoT and Embedded Systems enthusiast from Bangalore Campus. Active in Khel Society and Kaizen Tech.",
            avatarColorHex = 0xFF0D9488,
            coCurricularCredits = 16,
            rankTitle = "Club Contributor",
            role = UserRole.STUDENT,
            password = "password123"
        ),
        UserProfile(
            id = "peer_neha",
            name = "Neha Rathore",
            email = "neha.rathore@msot.edu",
            rollNo = "MSOT-2024-MG-033",
            campus = Campus.JAGANNATH,
            department = "School of Management & Business",
            academicYear = "2nd Year (Semester 4)",
            bio = "Entrepreneurship lead and startup enthusiast from Jaipur Campus. Event lead at Nexcell & Malhar.",
            avatarColorHex = 0xFFEA580C,
            coCurricularCredits = 22,
            rankTitle = "Venture Fellow",
            role = UserRole.STUDENT,
            password = "password123"
        ),
        UserProfile(
            id = "mgr_verma",
            name = "Dean S. K. Verma",
            email = "dean.verma@msot.edu",
            rollNo = "ADMIN-MGR-01",
            campus = Campus.HI_TECH,
            department = "Academic Affairs & Administration",
            academicYear = "Campus Administration",
            bio = "Campus Manager overseeing student activities, duty leave verifications, and inter-campus compliance.",
            avatarColorHex = 0xFF0F172A,
            coCurricularCredits = 100,
            rankTitle = "Dean of Student Affairs",
            role = UserRole.CAMPUS_MANAGER,
            password = "password123"
        )
    )

    val clubs = listOf(
        Club(
            id = "kaizen_tech",
            name = "Kaizen Tech",
            shortCode = "KT",
            category = ClubCategory.TECH,
            tagLine = "Computer Science & Modern Technology Club",
            description = "Kaizen Tech fosters technical excellence through competitive coding hackathons, applied AI models, open-source architectures, and hands-on system building.",
            facultyAdvisor = "Dr. R. Ramanujan (HOD, CS)",
            leadCoordinator = "Aryan Sharma",
            memberCount = 184,
            capacity = 250,
            meetingSchedule = "Every Saturday 10:00 AM - 1:00 PM",
            campus = Campus.HI_TECH,
            primaryColorHex = 0xFF0284C7,
            accentColorHex = 0xFFE0F2FE,
            objectives = listOf(
                "Host monthly 24h Hackathons and Algorithmic sprints",
                "Deploy production open-source tools for campus automation",
                "Hands-on workshops on Generative AI and Distributed Systems",
                "Peer mentorship for technical placements and research papers"
            )
        ),
        Club(
            id = "malhar",
            name = "Malhar",
            shortCode = "MLH",
            category = ClubCategory.CREATIVE,
            tagLine = "Creative, Cultural & Event Hosting Club",
            description = "Malhar is the vibrant artistic heartbeat of MSOT, hosting college festivals, stage dramas, musical productions, visual art showcases, and anchor teams.",
            facultyAdvisor = "Prof. Sunita Menon (Dean, Student Affairs)",
            leadCoordinator = "Ananya Sen",
            memberCount = 210,
            capacity = 300,
            meetingSchedule = "Every Saturday 1:30 PM - 4:30 PM",
            campus = Campus.HI_TECH,
            primaryColorHex = 0xFF9333EA,
            accentColorHex = 0xFFF3E8FF,
            objectives = listOf(
                "Produce the annual flagship cultural festival 'Gambade'",
                "Conduct music, dance, and theatrical productions",
                "Train emcees, creative writers, and multimedia visual artists",
                "Organize inter-college cultural exchanges across campuses"
            )
        ),
        Club(
            id = "khel_society",
            name = "Khel Society",
            shortCode = "KHEL",
            category = ClubCategory.SPORTS,
            tagLine = "Sports, Athletics & Fitness Society",
            description = "Khel Society champions athletic spirit, physical fitness, inter-campus tournaments in Football, Cricket, Badminton, Basketball, Chess, and competitive Esports.",
            facultyAdvisor = "Coach Devender Rana (Director of Physical Education)",
            leadCoordinator = "Rohan Verma",
            memberCount = 195,
            capacity = 280,
            meetingSchedule = "Every Saturday 7:00 AM - 10:00 AM",
            campus = Campus.HI_TECH,
            primaryColorHex = 0xFF059669,
            accentColorHex = 0xFFD1FAE5,
            objectives = listOf(
                "Organize MSOT Inter-Campus Champions Trophy",
                "Promote fitness regimes, marathon runs, and sportsmanship",
                "Host indoor tournaments in Table Tennis, Badminton & Chess",
                "Lead varsity esports leagues and competitive gaming"
            )
        ),
        Club(
            id = "nexcell",
            name = "Nexcell",
            shortCode = "NEX",
            category = ClubCategory.ENTREPRENEUR,
            tagLine = "Entrepreneurship, Innovation & Venture Club",
            description = "Nexcell empowers student founders with startup incubation, pitch deck teardowns, angel investor networking, and business model validation.",
            facultyAdvisor = "Dr. Vivek Singhal (Head, MSOT Incubation Cell)",
            leadCoordinator = "Priya Nair",
            memberCount = 142,
            capacity = 200,
            meetingSchedule = "Every Saturday 2:00 PM - 5:00 PM",
            campus = Campus.HI_TECH,
            primaryColorHex = 0xFFEA580C,
            accentColorHex = 0xFFFFEDD5,
            objectives = listOf(
                "Organize 'Cross Road' National Entrepreneurship Conclave",
                "Incubate early-stage student tech and commerce ventures",
                "Connect founders with alumni angels and venture mentors",
                "Conduct seed pitch competitions with non-dilutive grants"
            )
        )
    )

    val activities = listOf(
        ActivityPlan(
            id = "act_kt_01",
            clubId = "kaizen_tech",
            clubName = "Kaizen Tech",
            campus = Campus.HI_TECH,
            title = "Club Day: Microservices & Real-Time WebSockets Workshop",
            date = "Saturday, Oct 18, 2026",
            timeSlot = "10:00 AM - 1:00 PM",
            venue = "Lab 402, Turing Computer Center",
            topic = "Building Event-Driven Distributed Systems with Redis Streams",
            deliverableType = "GitHub Repo & Architecture PPT",
            deadline = "Monday, Oct 20, 11:59 PM",
            status = ActivityStatus.CHECK_IN_OPEN,
            advisorApproved = true,
            checkInCode = "842917"
        ),
        ActivityPlan(
            id = "act_mlh_01",
            clubId = "malhar",
            clubName = "Malhar",
            campus = Campus.HI_TECH,
            title = "Club Day: Gambade Theme & Stage Production Design",
            date = "Saturday, Oct 18, 2026",
            timeSlot = "1:30 PM - 4:30 PM",
            venue = "Main Auditorium Stage & Green Room",
            topic = "Audio-Visual Lighting Sequences and Anchoring Scripts",
            deliverableType = "Stage Cue Sheet & Creative Concept PDF",
            deadline = "Sunday, Oct 19, 6:00 PM",
            status = ActivityStatus.ANNOUNCED,
            advisorApproved = true,
            checkInCode = "519302"
        ),
        ActivityPlan(
            id = "act_khel_01",
            clubId = "khel_society",
            clubName = "Khel Society",
            campus = Campus.HI_TECH,
            title = "Club Day: Inter-Campus Badminton & Futsal Trials",
            date = "Saturday, Oct 18, 2026",
            timeSlot = "7:00 AM - 10:00 AM",
            venue = "Indoor Sports Complex Court A & B",
            topic = "Player Ranking Matrix & Physical Agility Benchmarks",
            deliverableType = "Fitness Log & Match Scorecard",
            deadline = "Saturday, Oct 18, 2:00 PM",
            status = ActivityStatus.ANNOUNCED,
            advisorApproved = true,
            checkInCode = "732049"
        ),
        ActivityPlan(
            id = "act_nex_01",
            clubId = "nexcell",
            clubName = "Nexcell",
            campus = Campus.HI_TECH,
            title = "Club Day: 3-Minute Lightning Pitch Teardowns",
            date = "Saturday, Oct 18, 2026",
            timeSlot = "2:00 PM - 5:00 PM",
            venue = "Executive Seminar Hall 2",
            topic = "Evaluating Unit Economics & Customer Acquisition Cost",
            deliverableType = "10-Slide Investor Deck & Financial Model",
            deadline = "Monday, Oct 20, 8:00 PM",
            status = ActivityStatus.SUBMISSION_OPEN,
            advisorApproved = true,
            checkInCode = "964183"
        ),
        ActivityPlan(
            id = "act_kt_rathinum_01",
            clubId = "kaizen_tech",
            clubName = "Kaizen Tech",
            campus = Campus.RATHINUM,
            title = "Club Day: Cloud Edge & Docker Containerization",
            date = "Saturday, Oct 18, 2026",
            timeSlot = "10:00 AM - 1:00 PM",
            venue = "Bangalore Tech Block - Computing Lab 3",
            topic = "Deploying Distributed Kubernetes Clusters on ARM Nodes",
            deliverableType = "Dockerfile & Helm Chart Config",
            deadline = "Monday, Oct 20, 10:00 PM",
            status = ActivityStatus.CHECK_IN_OPEN,
            advisorApproved = true,
            checkInCode = "472910"
        ),
        ActivityPlan(
            id = "act_mlh_jagannath_01",
            clubId = "malhar",
            clubName = "Malhar",
            campus = Campus.JAGANNATH,
            title = "Club Day: Rajasthani Folk Theatre & Stage Direction",
            date = "Saturday, Oct 18, 2026",
            timeSlot = "2:00 PM - 5:00 PM",
            venue = "Jaipur Heritage Auditorium Hall",
            topic = "Acoustic Stage Setups and Cultural Folk Choreography",
            deliverableType = "Stage Production Score & Lighting Script",
            deadline = "Sunday, Oct 19, 8:00 PM",
            status = ActivityStatus.ANNOUNCED,
            advisorApproved = true,
            checkInCode = "618392"
        )
    )

    val events = listOf(
        Event(
            id = "evt_01",
            title = "MSOT HackSprint 2026: 36H National Hackathon",
            clubId = "kaizen_tech",
            clubName = "Kaizen Tech",
            campus = Campus.HI_TECH,
            isInterCampus = true,
            description = "MSOT's premier inter-campus technical hackathon with tracks in Autonomous Systems, Decentralized Apps, and Healthcare AI. Total cash prize pool of $10,000.",
            date = "Nov 06 - 08, 2026",
            time = "9:00 AM onwards",
            venue = "Innovation Dome & Virtual Hub",
            registeredCount = 420,
            capacity = 500,
            ticketTiers = listOf(
                TicketTier("Student Standard", 10.0, "Hackathon Entry + Food & Beverages + SWAG Kit + Mentorship", 65),
                TicketTier("Early Bird Pass", 5.0, "Discounted full access for registered MSOT students", 15),
                TicketTier("VIP Hackathon Pass", 25.0, "Front row demo pitch, 1-on-1 VC speed dating, sponsor fast-track interview", 20)
            ),
            isDutyLeaveEligible = true,
            bannerGradientHex = Pair(0xFF0284C7, 0xFF312E81)
        ),
        Event(
            id = "evt_02",
            title = "Gambade 2026: Inter-University Cultural Extravaganza",
            clubId = "malhar",
            clubName = "Malhar",
            campus = Campus.HI_TECH,
            isInterCampus = true,
            description = "The largest annual cultural festival in the region featuring celebrity musical night, dance warfare, street theater, and digital art pavilions.",
            date = "Nov 20 - 22, 2026",
            time = "4:00 PM - 10:30 PM",
            venue = "MSOT Grand Open-Air Amphitheater",
            registeredCount = 1150,
            capacity = 1500,
            ticketTiers = listOf(
                TicketTier("General Fest Pass", 15.0, "All 3 days festival access + Concert entry", 280),
                TicketTier("Day Pass", 7.0, "Single day entry to dance and music performances", 70),
                TicketTier("All-Access Gold Pass", 35.0, "Front-stage VIP enclosure + meet & greet + official festival hoodie", 45)
            ),
            isDutyLeaveEligible = true,
            bannerGradientHex = Pair(0xFF9333EA, 0xFF4338CA)
        ),
        Event(
            id = "evt_03",
            title = "Cross Road 2026: Global Entrepreneurship Summit",
            clubId = "nexcell",
            clubName = "Nexcell",
            campus = Campus.HI_TECH,
            isInterCampus = true,
            description = "Connect with over 40 startup founders, angel syndicates, and unicorn leaders. Features live student venture pitches with seed funding commitments.",
            date = "Dec 04 - 05, 2026",
            time = "10:00 AM - 6:00 PM",
            venue = "Convention Center & Incubation Suites",
            registeredCount = 280,
            capacity = 350,
            ticketTiers = listOf(
                TicketTier("Delegate Pass", 12.0, "Access to keynote speeches, panel debates, and founder networking lunch", 50),
                TicketTier("Founder Pitch Seat", 20.0, "Guaranteed 5-minute pitch slot before angel jury + investor dossier", 20)
            ),
            isDutyLeaveEligible = true,
            bannerGradientHex = Pair(0xFFEA580C, 0xFFBE123C)
        ),
        Event(
            id = "evt_04",
            title = "MSOT Inter-Campus Champions Trophy (Sports Meet)",
            clubId = "khel_society",
            clubName = "Khel Society",
            campus = Campus.RATHINUM,
            isInterCampus = true,
            description = "Three days of intense collegiate athletic rivalry across Football, Cricket, Badminton, Volleyball, and Track & Field events between all campuses.",
            date = "Dec 18 - 20, 2026",
            time = "8:00 AM - 6:00 PM",
            venue = "Rathinum Central Sports Arena, Bangalore",
            registeredCount = 520,
            capacity = 800,
            ticketTiers = listOf(
                TicketTier("Spectator Pass", 3.0, "Entry to all stadium matches and finals", 200),
                TicketTier("Athlete Registration", 8.0, "Official participant kit, jersey & tournament entry", 80)
            ),
            isDutyLeaveEligible = true,
            bannerGradientHex = Pair(0xFF059669, 0xFF047857)
        )
    )

    val tickets = listOf(
        Ticket(
            id = "tkt_001",
            eventId = "evt_01",
            eventTitle = "MSOT HackSprint 2026",
            tierName = "Student Standard",
            amountPaid = 10.0,
            qrCodePayload = "MSOT-TKT-HACK26-94812-CHIRAG",
            attendeeName = "Chirag Sharma",
            rollNo = "MSOT-2023-CS-042",
            campus = Campus.HI_TECH,
            purchaseDate = "Oct 12, 2026",
            transactionId = "tx_stripe_94a82c1990f1",
            status = "CONFIRMED"
        )
    )

    val submissions = listOf(
        Submission(
            id = "sub_01",
            activityId = "act_kt_01",
            activityTitle = "Microservices & Real-Time WebSockets Workshop",
            clubName = "Kaizen Tech",
            studentId = "std_2026_042",
            studentName = "Chirag Sharma",
            fileTitle = "Distributed-Task-Queue-Redis.pdf",
            fileType = "PDF Architecture Report",
            resourceUrl = "https://github.com/chirushi/msot-distributed-queue",
            submittedAt = "Oct 12, 2026 at 4:15 PM",
            isLate = false,
            isEvaluated = true,
            score = 96,
            feedback = "Exceptional modularization, clean Docker Compose setup, and resilient WebSocket reconnection logic.",
            evaluatorName = "Dr. R. Ramanujan"
        ),
        Submission(
            id = "sub_02",
            activityId = "act_nex_01",
            activityTitle = "3-Minute Lightning Pitch Teardowns",
            clubName = "Nexcell",
            studentId = "std_2026_042",
            studentName = "Chirag Sharma",
            fileTitle = "EduSphere-AI-PitchDeck.pptx",
            fileType = "Presentation Deck",
            resourceUrl = "https://edusphere-msot.cloud/pitch.pdf",
            submittedAt = "Oct 13, 2026 at 11:20 AM",
            isLate = false,
            isEvaluated = false,
            score = null,
            feedback = null,
            evaluatorName = null
        )
    )

    val certificates = listOf(
        Certificate(
            id = "cert_01",
            certNumber = "MSOT-CERT-KT-2026-0841",
            studentName = "Chirag Sharma",
            rollNo = "MSOT-2023-CS-042",
            clubOrEventTitle = "Kaizen Tech - Advanced Systems Workshop",
            category = "Technical Excellence & Leadership",
            issueDate = "Sept 28, 2026",
            verificationHash = "SHA256:8f2a991bce04192bfa4081c78491d90c",
            signatoryName = "Dr. R. Ramanujan",
            signatoryRole = "Faculty Advisor, Kaizen Tech"
        ),
        Certificate(
            id = "cert_02",
            certNumber = "MSOT-CERT-NEX-2026-0312",
            studentName = "Chirag Sharma",
            rollNo = "MSOT-2023-CS-042",
            clubOrEventTitle = "Nexcell - Seed Pitch Cohort 2026",
            category = "Best Venture Innovation",
            issueDate = "Aug 15, 2026",
            verificationHash = "SHA256:39f1c78aa0e2b489d718449cfa39281a",
            signatoryName = "Dr. Vivek Singhal",
            signatoryRole = "Head, MSOT Incubation Cell"
        )
    )

    val attendanceRecords = listOf(
        AttendanceRecord(
            id = "att_01",
            activityId = "act_kt_01",
            studentId = "std_2026_042",
            studentName = "Chirag Sharma",
            rollNo = "MSOT-2023-CS-042",
            timestamp = "Oct 11, 2026 at 10:04 AM",
            status = AttendanceStatus.PRESENT,
            isLate = false,
            verificationToken = "QR_VERIFIED_7482"
        ),
        AttendanceRecord(
            id = "att_02",
            activityId = "act_khel_01",
            studentId = "std_2026_042",
            studentName = "Chirag Sharma",
            rollNo = "MSOT-2023-CS-042",
            timestamp = "Oct 04, 2026 at 7:14 AM",
            status = AttendanceStatus.PRESENT,
            isLate = false,
            verificationToken = "QR_VERIFIED_9103"
        )
    )

    val chatMessages = listOf(
        ChatMessage(
            id = "msg_01",
            channelId = "kaizen_tech",
            channelTitle = "Kaizen Tech General",
            senderId = "coord_aryan",
            senderName = "Aryan Sharma (Coordinator)",
            senderRole = "Student Coordinator",
            avatarColorHex = 0xFF0284C7,
            messageText = "Welcome everyone! Reminder: Saturday Club Day starts at 10:00 AM sharp in Turing Lab 402. Bring your laptops with Docker installed!",
            timestamp = "Yesterday at 6:45 PM",
            reactions = mapOf("👍" to 24, "🚀" to 18)
        ),
        ChatMessage(
            id = "msg_02",
            channelId = "kaizen_tech",
            channelTitle = "Kaizen Tech General",
            senderId = "adv_ramanujan",
            senderName = "Dr. R. Ramanujan",
            senderRole = "Faculty Advisor",
            avatarColorHex = 0xFF4338CA,
            messageText = "Activity plan for Microservices workshop is officially approved. Ensure the deliverable rubrics are followed.",
            timestamp = "Yesterday at 7:12 PM",
            reactions = mapOf("🙌" to 31)
        ),
        ChatMessage(
            id = "msg_03",
            channelId = "malhar",
            channelTitle = "Malhar Production & Auditions",
            senderId = "coord_ananya",
            senderName = "Ananya Sen",
            senderRole = "Coordinator",
            avatarColorHex = 0xFF9333EA,
            messageText = "Auditions for Gambade's anchor team and band rehearsals will be conducted in the Amphitheater tomorrow!",
            timestamp = "Today at 9:30 AM",
            reactions = mapOf("✨" to 19, "🎭" to 14)
        ),
        ChatMessage(
            id = "msg_04",
            channelId = "khel_society",
            channelTitle = "Khel Society Sports",
            senderId = "coord_rohan",
            senderName = "Rohan Verma",
            senderRole = "Coordinator",
            avatarColorHex = 0xFF059669,
            messageText = "Inter-campus Futsal selections starting Saturday 7:00 AM. Please wear proper non-marking sports shoes.",
            timestamp = "Today at 10:15 AM",
            reactions = mapOf("⚽" to 22, "🔥" to 11)
        ),
        ChatMessage(
            id = "msg_05",
            channelId = "nexcell",
            channelTitle = "Nexcell Founders Circle",
            senderId = "coord_priya",
            senderName = "Priya Nair",
            senderRole = "Coordinator",
            avatarColorHex = 0xFFEA580C,
            messageText = "Cross Road 2026 pitch submissions are now live! We've lined up 5 prominent alumni angel investors.",
            timestamp = "Today at 11:00 AM",
            reactions = mapOf("💡" to 16, "📈" to 25)
        ),
        ChatMessage(
            id = "msg_net_01",
            channelId = "networking",
            channelTitle = "Peer Networking (E2EE)",
            senderId = "peer_vikram",
            senderName = "Vikram Patel",
            senderRole = "Fellow Member (Rathinum Campus, Bangalore)",
            avatarColorHex = 0xFF0D9488,
            messageText = "Hey Chirag! Saw your distributed queue submission on GitHub, really clean work! Want to team up for HackSprint 2026?",
            timestamp = "10 mins ago",
            isDirect = true,
            isEncrypted = true,
            reactions = mapOf("🤝" to 1)
        )
    )

    val notifications = listOf(
        AppNotification(
            id = "notif_01",
            title = "Club Day Attendance Open",
            message = "Kaizen Tech Saturday session check-in is now live. Use your 6-digit PIN or scan coordinator QR.",
            timestamp = "15m ago",
            type = "EVENT",
            isRead = false
        ),
        AppNotification(
            id = "notif_02",
            title = "Ticket Confirmed: HackSprint 2026",
            message = "Your Student Standard pass is confirmed. Transaction ID: tx_stripe_94a82c1990f1.",
            timestamp = "2h ago",
            type = "TICKET",
            isRead = false
        ),
        AppNotification(
            id = "notif_03",
            title = "Grade Published for Workshop",
            message = "Dr. Ramanujan scored your Microservices report: 96/100 with commendation feedback.",
            timestamp = "1d ago",
            type = "APPROVAL",
            isRead = true
        )
    )

    val dutyLeaveRequests = listOf(
        DutyLeaveRequest(
            id = "dl_01",
            studentId = "std_2026_042",
            studentName = "Chirag Sharma",
            rollNo = "MSOT-2023-CS-042",
            campus = Campus.HI_TECH,
            eventId = "evt_01",
            eventTitle = "MSOT HackSprint 2026: 36H National Hackathon",
            dates = "Nov 06 - 08, 2026",
            reason = "Official university finalist delegation for 36H competitive hackathon tracks.",
            status = DutyLeaveStatus.APPROVED,
            requestedAt = "Oct 12, 2026",
            reviewedBy = "Dean S. K. Verma",
            reviewNotes = "Authorized under inter-campus competitive representation policy."
        ),
        DutyLeaveRequest(
            id = "dl_02",
            studentId = "std_2026_042",
            studentName = "Chirag Sharma",
            rollNo = "MSOT-2023-CS-042",
            campus = Campus.HI_TECH,
            eventId = "evt_04",
            eventTitle = "MSOT Inter-Campus Champions Trophy (Sports Meet)",
            dates = "Dec 18 - 20, 2026",
            reason = "Varsity football contingent participation at Rathinum Campus Bangalore.",
            status = DutyLeaveStatus.PENDING,
            requestedAt = "Oct 13, 2026"
        )
    )

    val eventTeams = listOf(
        EventTeam(
            id = "team_01",
            eventId = "evt_01",
            eventTitle = "MSOT HackSprint 2026",
            teamName = "CyberKnights",
            joinCode = "CK88",
            leaderId = "std_2026_042",
            leaderName = "Chirag Sharma",
            members = listOf("Chirag Sharma (Leader)", "Vikram Patel", "Aryan Sharma"),
            maxMembers = 4
        ),
        EventTeam(
            id = "team_02",
            eventId = "evt_03",
            eventTitle = "Cross Road 2026",
            teamName = "EduSphere Founders",
            joinCode = "ES26",
            leaderId = "std_2026_042",
            leaderName = "Chirag Sharma",
            members = listOf("Chirag Sharma (Leader)", "Neha Rathore"),
            maxMembers = 3
        )
    )
}

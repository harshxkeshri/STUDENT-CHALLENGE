package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.model.Campus
import com.example.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ActivityPlanSuggestion(
    val title: String,
    val topic: String,
    val venueSuggestion: String,
    val timeSlotSuggestion: String,
    val deliverableType: String,
    val suggestedDeadline: String,
    val rubricHighlights: List<String>
)

data class TranscriptAiSummary(
    val executiveSummary: String,
    val topCompetencies: List<String>,
    val resumeBulletPoints: List<String>,
    val careerRecommendation: String
)

class GeminiAiService private constructor() {

    companion object {
        private const val TAG = "GeminiAiService"
        // MANDATORY model as per gemini-api skill rules for text generation tasks
        private const val MODEL_NAME = "gemini-3.5-flash"
        private const val API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

        val instance: GeminiAiService by lazy { GeminiAiService() }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Core method to generate text using Gemini API
     */
    suspend fun generateContent(prompt: String, systemInstruction: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured or using default placeholder.")
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured in Secrets panel.")
            )
        }

        try {
            val rootJson = JSONObject()

            // System Instruction if provided
            if (!systemInstruction.isNullOrBlank()) {
                val sysInstObj = JSONObject()
                val sysPartsArray = JSONArray()
                sysPartsArray.put(JSONObject().put("text", systemInstruction))
                sysInstObj.put("parts", sysPartsArray)
                rootJson.put("systemInstruction", sysInstObj)
            }

            // Main contents
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", prompt))
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            genConfig.put("maxOutputTokens", 1024)
            rootJson.put("generationConfig", genConfig)

            val url = "$API_BASE_URL?key=$apiKey"
            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API returned error ${response.code}: $responseBody")
                return@withContext Result.failure(Exception("Gemini API error (${response.code})"))
            }

            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    return@withContext Result.success(text)
                }
            }

            Result.failure(Exception("No content returned from Gemini model"))
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call", e)
            Result.failure(e)
        }
    }

    /**
     * Ask Campus Copilot a question with contextual awareness
     */
    suspend fun askCampusCopilot(
        userPrompt: String,
        userRole: UserRole,
        campus: Campus,
        history: List<Pair<String, String>> = emptyList()
    ): String {
        val systemPrompt = """
            You are CampusCopilot, an intelligent AI co-curricular assistant for MSOT's multi-campus community (CampusCred).
            Current User Role: ${userRole.label}
            Current Campus: ${campus.displayName} (${campus.city})
            Key Context:
            - MSOT has 4 primary club domains: Technology (Kaizen Tech, NexCell), Creative (Malhar, Canvas Crew), Sports (Khel Society), and Entrepreneurship (Incubate MSOT).
            - Attendance is verified using rotating 6-digit dynamic codes and QR tokens during Club Days.
            - Students earn verified Co-Curricular Credits (min 20 credits required for honors transcripts).
            - Faculty Advisors review activity plans, rubrics, and deliverable submissions.
            - Keep responses encouraging, well-structured, concise, and helpful. Use clear bullet points and markdown.
        """.trimIndent()

        val promptBuilder = StringBuilder()
        if (history.isNotEmpty()) {
            promptBuilder.append("Conversation context:\n")
            history.takeLast(4).forEach { (q, a) ->
                promptBuilder.append("User: $q\nAssistant: $a\n\n")
            }
        }
        promptBuilder.append("User question: $userPrompt")

        val result = generateContent(promptBuilder.toString(), systemPrompt)
        return result.getOrElse {
            // Intelligent fallback with tailored helpful campus advice
            getFallbackCampusCopilotResponse(userPrompt, userRole, campus)
        }
    }

    /**
     * Generate an Activity Plan for a club
     */
    suspend fun generateActivityPlan(
        clubName: String,
        category: String,
        topicHint: String,
        campus: Campus
    ): ActivityPlanSuggestion {
        val prompt = """
            Generate an engaging, hands-on university club activity plan for:
            Club Name: $clubName ($category)
            Campus: ${campus.displayName}
            Topic Hint / Theme: ${topicHint.ifBlank { "Modern hands-on workshop with project deliverables" }}
            
            Return your response in strict JSON format with keys:
            {
               "title": "A catchy title (max 8 words)",
               "topic": "Concise summary of hands-on learning goals (1-2 sentences)",
               "venueSuggestion": "Realistic campus venue (e.g., Turing Lab 2, Main Auditorium, Design Studio)",
               "timeSlotSuggestion": "Realistic 2-3 hour slot (e.g., Saturday 10:00 AM - 1:00 PM)",
               "deliverableType": "Measurable student deliverable (e.g., GitHub Repo & Live Demo, Video Reel, Pitch Deck PDF)",
               "suggestedDeadline": "Submission deadline (e.g., Monday 11:59 PM)",
               "rubricHighlights": ["Criterion 1 (40%)", "Criterion 2 (30%)", "Criterion 3 (30%)"]
            }
            Do not include markdown ticks around the json.
        """.trimIndent()

        val result = generateContent(prompt)
        val text = result.getOrNull()
        if (!text.isNullOrBlank()) {
            try {
                val cleanJson = text.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()
                val obj = JSONObject(cleanJson)
                val rubrics = mutableListOf<String>()
                val rubricsArray = obj.optJSONArray("rubricHighlights")
                if (rubricsArray != null) {
                    for (i in 0 until rubricsArray.length()) {
                        rubrics.add(rubricsArray.getString(i))
                    }
                }
                return ActivityPlanSuggestion(
                    title = obj.optString("title", "$clubName: Hands-on Workshop"),
                    topic = obj.optString("topic", "Hands-on collaborative project and presentation"),
                    venueSuggestion = obj.optString("venueSuggestion", "Tech Innovation Lab"),
                    timeSlotSuggestion = obj.optString("timeSlotSuggestion", "Saturday, 10:00 AM - 1:00 PM"),
                    deliverableType = obj.optString("deliverableType", "Project Report & Code/Design"),
                    suggestedDeadline = obj.optString("suggestedDeadline", "Monday, 11:59 PM"),
                    rubricHighlights = if (rubrics.isNotEmpty()) rubrics else listOf("Technical Rigor (40%)", "Presentation (30%)", "Teamwork (30%)")
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse Gemini JSON output", e)
            }
        }

        // High quality fallback
        val defaultTopic = if (topicHint.isNotBlank()) topicHint else "Accelerated Innovation & Project Showcase"
        return ActivityPlanSuggestion(
            title = "$clubName: $defaultTopic",
            topic = "Deep-dive practical implementation, collaborative troubleshooting, and verified project output for $clubName members.",
            venueSuggestion = "Innovation Hub, Room 304",
            timeSlotSuggestion = "Saturday, 10:00 AM - 1:00 PM",
            deliverableType = "Project Deliverable & Slide Deck",
            suggestedDeadline = "Monday, 11:59 PM",
            rubricHighlights = listOf(
                "Execution Depth (40 pts)",
                "Clarity of Deliverable (30 pts)",
                "Punctual Check-In & Participation (30 pts)"
            )
        )
    }

    /**
     * Generate an Executive Co-Curricular Transcript & Resume Summary
     */
    suspend fun generateTranscriptSummary(
        studentName: String,
        rollNo: String,
        department: String,
        credits: Int,
        attendedCount: Int,
        submissionsCount: Int,
        certsCount: Int
    ): TranscriptAiSummary {
        val prompt = """
            Student: $studentName ($rollNo)
            Department: $department
            Verified Co-Curricular Stats:
            - Credits Earned: $credits credits
            - Club Activities Attended: $attendedCount
            - Verified Project Submissions: $submissionsCount
            - Issued Certificates of Excellence: $certsCount

            Generate a professional transcript summary and resume impact bullet points.
            Return strict JSON:
            {
               "executiveSummary": "2-3 sentences summarizing leadership, multidisciplinary engagement, and practical problem solving.",
               "topCompetencies": ["Skill 1", "Skill 2", "Skill 3", "Skill 4"],
               "resumeBulletPoints": [
                 "Action-verb oriented resume bullet point with metric",
                 "Action-verb oriented resume bullet point with metric",
                 "Action-verb oriented resume bullet point with metric"
               ],
               "careerRecommendation": "1-2 sentence recommendation on ideal industry roles."
            }
            Do not include markdown codeblocks.
        """.trimIndent()

        val result = generateContent(prompt)
        val text = result.getOrNull()
        if (!text.isNullOrBlank()) {
            try {
                val cleanJson = text.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()
                val obj = JSONObject(cleanJson)
                val competencies = mutableListOf<String>()
                val compArray = obj.optJSONArray("topCompetencies")
                if (compArray != null) {
                    for (i in 0 until compArray.length()) competencies.add(compArray.getString(i))
                }
                val bullets = mutableListOf<String>()
                val bulletArray = obj.optJSONArray("resumeBulletPoints")
                if (bulletArray != null) {
                    for (i in 0 until bulletArray.length()) bullets.add(bulletArray.getString(i))
                }
                return TranscriptAiSummary(
                    executiveSummary = obj.optString("executiveSummary", "$studentName has demonstrated sustained excellence across verified university club activities and project work."),
                    topCompetencies = if (competencies.isNotEmpty()) competencies else listOf("Project Leadership", "Cross-Functional Collaboration", "Technical Prototyping", "Peer Mentorship"),
                    resumeBulletPoints = if (bullets.isNotEmpty()) bullets else listOf(
                        "Achieved $credits verified co-curricular credits across $attendedCount institutional club activities.",
                        "Delivered $submissionsCount evaluated technical & creative deliverables with top rubric evaluations.",
                        "Awarded $certsCount verified institutional certificates for excellence in extracurricular leadership."
                    ),
                    careerRecommendation = obj.optString("careerRecommendation", "Strongly recommended for roles in engineering leadership, systems architecture, or product management.")
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse transcript summary JSON", e)
            }
        }

        // Smart fallback
        return TranscriptAiSummary(
            executiveSummary = "$studentName ($department) has completed $credits verified co-curricular credits, exhibiting high commitment to campus leadership, hands-on problem solving, and interdisciplinary collaboration.",
            topCompetencies = listOf("Technical Leadership", "Agile Collaboration", "Rapid Prototyping", "Public Presentation"),
            resumeBulletPoints = listOf(
                "Completed $attendedCount verified club workshops and hackathons, accumulating $credits institutional credits.",
                "Engineered and submitted $submissionsCount evaluated project deliverables under faculty advisor oversight.",
                "Earned $certsCount verified digital credentials authenticated on the MSOT institutional registry."
            ),
            careerRecommendation = "Highly suited for fast-paced technical and collaborative leadership roles in competitive tech organizations."
        )
    }

    /**
     * Generate an Advisor Feedback on a student submission
     */
    suspend fun generateAdvisorFeedback(
        activityTitle: String,
        topic: String,
        deliverableType: String,
        score: Int
    ): String {
        val prompt = """
            As a university faculty advisor, write a 2-sentence encouraging, constructive feedback review for a student deliverable.
            Activity: $activityTitle
            Topic: $topic
            Deliverable Type: $deliverableType
            Score: $score / 100
            Keep tone professional, encouraging, and specific to the score.
        """.trimIndent()

        val result = generateContent(prompt)
        return result.getOrElse {
            if (score >= 90) {
                "Exceptional execution on the $deliverableType! The depth of understanding demonstrated in $topic reflects thorough preparation and strong practical rigor."
            } else if (score >= 80) {
                "Very commendable effort on $activityTitle. Good structural clarity; consider refining the edge-case validation and documentation in future iterations."
            } else {
                "Good start on the deliverable. Focus on addressing all rubric milestones more comprehensively and consult the coordinator for hands-on tips."
            }
        }
    }

    /**
     * Intelligent local fallback for CampusCopilot
     */
    private fun getFallbackCampusCopilotResponse(
        prompt: String,
        role: UserRole,
        campus: Campus
    ): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("credit") || lower.contains("point") -> """
                **Verified Co-Curricular Credits at MSOT:**
                - Students earn **2 to 5 credits** per verified Club Day workshop attendance.
                - Submitting an evaluated project deliverable awards up to **10 credits**.
                - A minimum of **20 credits** is required for the Official Co-Curricular Transcript seal.
                - All credits are timestamped and signed with cryptographic verification tokens.
            """.trimIndent()

            lower.contains("check-in") || lower.contains("qr") || lower.contains("attendance") -> """
                **How Club Day Check-In Works:**
                1. Coordinators launch the **QR Station** at the club venue.
                2. A dynamic 6-digit passcode rotates periodically.
                3. Students tap **Check-In** on the Home or Events tab, scan the QR code or enter the 6-digit code.
                4. On-time check-ins mark you **Present**; check-ins after 15 minutes are recorded as **Late**.
            """.trimIndent()

            lower.contains("duty leave") || lower.contains("leave") -> """
                **Duty Leave Eligibility & Process:**
                - Duty leave is automatically requested when you purchase an authorized Inter-Campus or Competition ticket.
                - Campus Managers review and stamp duty leaves under the **Duty & Alerts** tab.
                - Approved requests update your university academic portal attendance automatically.
            """.trimIndent()

            lower.contains("join") || lower.contains("club") -> """
                **Campus Clubs at ${campus.displayName}:**
                - **Kaizen Tech**: AI, Cloud, Quantum & Full-Stack Systems
                - **NexCell**: Biotech, Robotics & Hardware Prototyping
                - **Malhar**: Cultural Arts, Theater & Acoustic Music
                - **Khel Society**: Inter-collegiate Athletics & Esports
                - **Incubate MSOT**: Startup Incubation & Pitch Competitions
                
                You can submit a membership request directly from the **Clubs** tab!
            """.trimIndent()

            else -> """
                **CampusCopilot AI Insight:**
                Welcome to ${campus.displayName}! Whether you're planning an event, checking attendance requirements, or preparing your co-curricular transcript, I'm here to help.
                
                *Tip:* For full live Gemini AI responses, make sure your `GEMINI_API_KEY` is configured in the AI Studio Secrets panel.
            """.trimIndent()
        }
    }
}

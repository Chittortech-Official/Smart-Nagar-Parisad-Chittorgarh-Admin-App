package com.chittortech.snp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Department(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    @SerialName("default_sla_hours")
    val defaultSlaHours: Int = 48,
    @SerialName("head_officer_name")
    val headOfficerName: String? = null,
    @SerialName("head_officer_phone")
    val headOfficerPhone: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class Ward(
    @SerialName("ward_number")
    val wardNumber: Int = 1,
    @SerialName("ward_name")
    val wardName: String = "",
    @SerialName("councillor_name")
    val councillorName: String? = null,
    @SerialName("councillor_phone")
    val councillorPhone: String? = null,
    val zone: String? = "Central Zone",
    @SerialName("key_landmarks")
    val keyLandmarks: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class UserProfile(
    val id: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val email: String = "",
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    val role: String = "",
    @SerialName("ward_number")
    val wardNumber: Int? = null,
    @SerialName("department_id")
    val departmentId: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class Ticket(
    val id: String = "",
    @SerialName("ticket_number")
    val ticketNumber: String = "",
    @SerialName("intake_id")
    val intakeId: String? = null,
    @SerialName("citizen_name")
    val citizenName: String = "",
    @SerialName("citizen_phone")
    val citizenPhone: String = "",
    @SerialName("ward_number")
    val wardNumber: Int = 1,
    val address: String = "",
    val landmark: String? = null,
    @SerialName("category_id")
    val categoryId: String = "",
    @SerialName("department_id")
    val departmentId: String = "",
    @SerialName("problem_description")
    val problemDescription: String = "",
    val status: String = "REGISTERED",
    @SerialName("forwarding_option")
    val forwardingOption: String = "COUNCILLOR_AND_OFFICER",
    @SerialName("sla_hours")
    val slaHours: Int = 48,
    @SerialName("sla_deadline")
    val slaDeadline: String = "",
    @SerialName("is_overdue")
    val isOverdue: Boolean = false,
    @SerialName("is_escalated")
    val isEscalated: Boolean = false,
    @SerialName("escalation_notes")
    val escalationNotes: String? = null,
    @SerialName("assigned_officer_id")
    val assignedOfficerId: String? = null,
    @SerialName("registration_time")
    val registrationTime: String? = null,
    @SerialName("resolution_time")
    val resolutionTime: String? = null,
    @SerialName("closed_at")
    val closedAt: String? = null,
    @SerialName("created_by")
    val createdBy: String? = null
)

@Serializable
data class NotificationLog(
    val id: String = "",
    @SerialName("ticket_id")
    val ticketId: String? = null,
    @SerialName("recipient_type")
    val recipientType: String = "",
    @SerialName("recipient_phone")
    val recipientPhone: String = "",
    val channel: String = "WHATSAPP",
    @SerialName("template_name")
    val templateName: String? = null,
    @SerialName("message_content")
    val messageContent: String = "",
    val status: String = "SENT",
    @SerialName("sent_at")
    val sentAt: String? = null
)

data class DashboardStats(
    val todayRegistered: Int = 0,
    val totalComplaints: Int = 0,
    val pendingComplaints: Int = 0,
    val inProgressComplaints: Int = 0,
    val todayClosed: Int = 0,
    val monthClosed: Int = 0,
    val yearClosed: Int = 0,
    val overdueComplaints: Int = 0,
    val escalatedComplaints: Int = 0,
    val resolutionRatePercent: Int = 0
)

data class DepartmentStats(
    val department: Department,
    val total: Int = 0,
    val pending: Int = 0,
    val inProgress: Int = 0,
    val closed: Int = 0,
    val overdue: Int = 0,
    val resolutionRatePercent: Int = 0
)


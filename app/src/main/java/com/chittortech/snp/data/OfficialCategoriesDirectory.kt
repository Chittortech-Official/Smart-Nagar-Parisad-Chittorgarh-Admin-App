package com.chittortech.snp.data

import com.chittortech.snp.data.models.ComplaintCategory

data class CategoryMeta(
    val name: String,
    val hindiName: String,
    val departmentCode: String,
    val defaultSlaHours: Int,
    val isEmergency: Boolean = false,
    val emoji: String
)

object OfficialCategoriesDirectory {

    val OFFICIAL_CATEGORIES = listOf(
        CategoryMeta(
            name = "Kachra Patra Emptying & Overflow",
            hindiName = "कचरा पात्र खाली करना एवं ओवरफ्लो",
            departmentCode = "SAN",
            defaultSlaHours = 24,
            emoji = "🗑️"
        ),
        CategoryMeta(
            name = "Door to Door Waste Collection Vehicle Missed",
            hindiName = "डोर-टू-डोर कचरा संग्रहण टिपर अनुपस्थित",
            departmentCode = "SAN",
            defaultSlaHours = 24,
            emoji = "🚛"
        ),
        CategoryMeta(
            name = "Dead Animal Disposal (Emergency)",
            hindiName = "मृत पशु निस्तारण (आपातकालीन 12 घंटे)",
            departmentCode = "SAN",
            defaultSlaHours = 12,
            isEmergency = true,
            emoji = "🚨"
        ),
        CategoryMeta(
            name = "Streetlight Dark / Defective LED",
            hindiName = "स्ट्रीट लाइट बंद / खराब एलईडी पोल",
            departmentCode = "ELE",
            defaultSlaHours = 48,
            emoji = "💡"
        ),
        CategoryMeta(
            name = "High-Mast Tower Light Failure",
            hindiName = "हाई-मास्ट चौक लाइट बंद / खराबी",
            departmentCode = "ELE",
            defaultSlaHours = 48,
            emoji = "🗼"
        ),
        CategoryMeta(
            name = "Pothole & Damaged Road Patch",
            hindiName = "सड़क गड्ढा एवं क्षतिग्रस्त पेच मरम्मत",
            departmentCode = "ROA",
            defaultSlaHours = 72,
            emoji = "🛣️"
        ),
        CategoryMeta(
            name = "Broken Divider / Pavement Repair",
            hindiName = "टूटा डिवाइडर / फुटपाथ मरम्मत",
            departmentCode = "ROA",
            defaultSlaHours = 72,
            emoji = "🚧"
        ),
        CategoryMeta(
            name = "Choked Open Drain / Nullah Overflow",
            hindiName = "खुली नाली / नाला अवरोध एवं ओवरफ्लो",
            departmentCode = "DRN",
            defaultSlaHours = 48,
            emoji = "🌊"
        ),
        CategoryMeta(
            name = "Missing or Broken Gutter Slabs",
            hindiName = "टूटी या खुली गटर पट्टियां (सुरक्षा खतरा)",
            departmentCode = "DRN",
            defaultSlaHours = 24,
            emoji = "⚠️"
        ),
        CategoryMeta(
            name = "Monsoon Waterlogging on Main Road",
            hindiName = "मुख्य सड़क पर जलभराव एवं निकासी",
            departmentCode = "WAT",
            defaultSlaHours = 24,
            emoji = "🌧️"
        ),
        CategoryMeta(
            name = "Illegal Footpath Encroachment / Tin Shed",
            hindiName = "फुटपाथ / आम रास्ते पर अवैध अतिक्रमण",
            departmentCode = "ENC",
            defaultSlaHours = 72,
            emoji = "🛑"
        )
    )

    fun getCategoryMeta(categoryId: String, problemDesc: String): CategoryMeta {
        // Try matching by problem description keywords if categoryId is arbitrary
        val descLower = problemDesc.lowercase()
        return when {
            descLower.contains("animal") || descLower.contains("carcass") || descLower.contains("मृत") -> OFFICIAL_CATEGORIES[2]
            descLower.contains("door") || descLower.contains("vehicle") || descLower.contains("tipper") || descLower.contains("टिपर") -> OFFICIAL_CATEGORIES[1]
            descLower.contains("light") || descLower.contains("led") || descLower.contains("pole") || descLower.contains("रोशनी") -> OFFICIAL_CATEGORIES[3]
            descLower.contains("high-mast") || descLower.contains("tower") -> OFFICIAL_CATEGORIES[4]
            descLower.contains("road") || descLower.contains("pothole") || descLower.contains("गड्ढा") || descLower.contains("सड़क") -> OFFICIAL_CATEGORIES[5]
            descLower.contains("divider") || descLower.contains("pavement") || descLower.contains("फुटपाथ") -> OFFICIAL_CATEGORIES[6]
            descLower.contains("drain") || descLower.contains("nullah") || descLower.contains("choked") || descLower.contains("नाली") -> OFFICIAL_CATEGORIES[7]
            descLower.contains("slab") || descLower.contains("gutter") || descLower.contains("manhole") || descLower.contains("गटर") -> OFFICIAL_CATEGORIES[8]
            descLower.contains("water") || descLower.contains("log") || descLower.contains("जलभराव") -> OFFICIAL_CATEGORIES[9]
            descLower.contains("encroach") || descLower.contains("shed") || descLower.contains("अतिक्रमण") -> OFFICIAL_CATEGORIES[10]
            else -> OFFICIAL_CATEGORIES[0] // Default Kachra Patra Emptying
        }
    }
}


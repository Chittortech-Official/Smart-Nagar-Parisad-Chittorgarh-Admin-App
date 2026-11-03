package com.chittortech.snp.data

import com.chittortech.snp.data.models.Department

object OfficialDepartmentsDirectory {
    val list: List<Department> = listOf(
        Department(
            id = "dept-san",
            name = "Sanitation & Garbage Collection",
            code = "SAN",
            defaultSlaHours = 24,
            headOfficerName = "Ramesh Sharma",
            headOfficerPhone = "+919414000101"
        ),
        Department(
            id = "dept-ele",
            name = "Street Lighting & Electrical",
            code = "ELE",
            defaultSlaHours = 48,
            headOfficerName = "Suresh Chandra Verma",
            headOfficerPhone = "+919414000102"
        ),
        Department(
            id = "dept-roa",
            name = "Roads & Civil Infrastructure",
            code = "ROA",
            defaultSlaHours = 72,
            headOfficerName = "Mahesh Jat",
            headOfficerPhone = "+919414000103"
        ),
        Department(
            id = "dept-drn",
            name = "Drains & Sewerage Management",
            code = "DRN",
            defaultSlaHours = 48,
            headOfficerName = "Kailash Meena",
            headOfficerPhone = "+919414000104"
        ),
        Department(
            id = "dept-wat",
            name = "Waterlogging & Drainage Emergency",
            code = "WAT",
            defaultSlaHours = 24,
            headOfficerName = "Dinesh Sen",
            headOfficerPhone = "+919414000105"
        ),
        Department(
            id = "dept-enc",
            name = "Encroachment & Land Monitoring",
            code = "ENC",
            defaultSlaHours = 72,
            headOfficerName = "Vikram Singh Rathore",
            headOfficerPhone = "+919414000106"
        )
    )
}


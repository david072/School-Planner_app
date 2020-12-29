package de.sharable.android.school_planner.util

import android.content.res.Resources
import de.sharable.android.school_planner.R

enum class Subject(val abbreviation: String, val color: Int, val actualName: Int) {

    BIOLOGY("Bio", R.color.subject_biology, R.string.subject_biology),
    NWT("NwT", R.color.subject_nwt, R.string.subject_nwt),
    IMP("IMP", R.color.subject_imp, R.string.subject_imp),
    SPANISH("Sp", R.color.subject_spanish, R.string.subject_spanish),
    MATHEMATICS("M", R.color.subject_mathematics, R.string.subject_mathematics),
    GERMAN("D", R.color.subject_german, R.string.subject_german),
    ENGLISH("E", R.color.subject_english, R.string.subject_english),
    HISTORY("G", R.color.subject_history, R.string.subject_history),
    LATIN("L", R.color.subject_latin, R.string.subject_latin),
    FRENCH("F", R.color.subject_french, R.string.subject_french),
    PHYSICS("Ph", R.color.subject_physics, R.string.subject_physics),
    SPORTS("S", R.color.subject_sports, R.string.subject_sports),
    GEOGRAPHY("Ek", R.color.subject_geography, R.string.subject_geography),
    EVANGELICAL_RELIGION("evR", R.color.subject_evangelical_religion, R.string.subject_evangelical_religion),
    CATHOLIC_RELIGION("kR", R.color.subject_catholic_religion, R.string.subject_catholic_religion),
    ETHNICS("Eth", R.color.subject_ethnics, R.string.subject_ethnics),
    GKWBS("GkWBS", R.color.subject_gkwbs, R.string.subject_gkwbs),
    CHEMISTRY("Ch", R.color.subject_chemistry, R.string.subject_chemistry);

    companion object {
        fun fromAbbreviation(abbreviation: String): Subject? {
            for (subject: Subject in values()) {
                if(subject.abbreviation == abbreviation) {
                    return subject
                }
            }

            return null
        }

        fun fromName(name: String, resources: Resources): Subject? {
            for(subject: Subject in values()) {
                if(resources.getString(subject.actualName) == name) {
                    return subject
                }
            }

            return null
        }

        fun toStringArray(resources: Resources): Array<String> {
            return Array(values().size) { i -> resources.getString(values()[i].actualName) }
        }
    }

}
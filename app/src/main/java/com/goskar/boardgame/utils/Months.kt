package com.goskar.boardgame.utils

enum class Months (val monthsName: String, val monthsNumber: Int) {
    JANUARY ("Jan",1),
    FEBRUARY("Feb",2),
    MARCH("Mar",3),
    APRIL("Apr",4),
    MAY("May",5),
    JUNE("June",6),
    JULY("July",7),
    AUGUST("Aug",8),
    SEPTEMBER("Sept",9),
    OCTOBER("Oct",10),
    NOVEMBER("Nov",11),
    DECEMBER("Dec",12);

    companion object{
        fun getMonthByNumber(monthsNumber: Int): Months? {
            return Months.entries.find { it.monthsNumber == monthsNumber }
        }
    }
}
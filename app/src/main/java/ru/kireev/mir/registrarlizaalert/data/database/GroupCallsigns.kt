package ru.kireev.mir.registrarlizaalert.data.database

enum class GroupCallsigns (val nameOfGroup: String) {
    BORT("Bort"), // Авиация, вертолеты, коптеры
    KINOLOG("Kinolog"), // Кинологи
    VETER("Veter"), // Авто, джипы, квадроциклы, шерп
    VODA("Voda"), // Водный транспорт и водолазы
    PEGAS("Pegas"), // Конноу подразделение
    POLICE("Police"), // Полиция без старших ЛизаАлерт
    MCHS("Mchs"), // МЧС без старших ЛизаАлерт
    PSO("Pso"), // ПСО без старших ЛизаАлерт
    LISA("Lisa") // Пешие группы со старшими ЛизаАлерт
}


/**
 * Created by Алексей on 26.04.2017.
 */
public enum Names {
    Adrian(0),
    Aleksandr(1),
    Aleksey(2),
    Anatoliy(3),
    Andrey(4),
    Anton(5),
    Arkadiy(6),
    Arseniy(7),
    Artemiy(8),
    Bogdan(9),
    Boris(10),
    Vladimir(11),
    Vadim(12),
    Valeriy(13),
    Vasiliy(14),
    Viktor(15),
    Gennadiy(16),
    Grigoriy(17),
    Denis(18),
    Dmitriy(19),
    Ivan(20),
    Igor(21),
    Illia(22),
    Konstantin(23),
    Leonid(24),
    Maksim(25),
    Mikhail(26),
    Nikita(27),
    Nikolay(28),
    Oleg(29),
    Orest(30),
    Pavel(31),
    Piotr(32),
    Potap(33),
    Prokhor(34),
    Ruslan(35),
    Roman(36),
    Rostislav(37),
    Semion(38),
    Sergey(39),
    Stanislav(40),
    Taras(41),
    Timofey(42),
    Trofim(43),
    Fiodor(44),
    Filipp(45),
    Eduard(46),
    Yuriy(47),
    Yakov(48),
    Yaroslav(49);

    private final int id;

    Names(int code) {
        id = code;
    }
    public int getId()
    {
        return id;
    }
    public static Names fromId(int code)
    {
        Names[] list = Names.values();

        if (code >= 0 && code < list.length)
            return list[code];
        else
            return Names.Adrian;
    }
}

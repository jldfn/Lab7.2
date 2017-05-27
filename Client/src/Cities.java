/**
 * Created by aal on 27.04.2017.
 */
public enum Cities {
    Moscow(0),
    Saint_Petersburg(1),
    Novosibirsk(2),
    Yekaterinburg(3),
    Nizhny_Novgorod(4),
    Samara(5),
    Omsk(6),
    Kazan(7),
    Chelyabinsk(8),
    Rostov_on_Don(9),
    Ufa(10),
    Volgograd(11),
    Perm(12),
    Krasnoyarsk(13),
    Voronezh(14),
    Saratov(15),
    Krasnodar(16),
    Tolyatti(17),
    Izhevsk(18),
    Ulyanovsk(19),
    Barnaul(20),
    Vladivostok(21),
    Yaroslavl(22),
    Irkutsk(23),
    Tyumen(24),
    Khabarovsk(25),
    Makhachkala(26),
    Orenburg(27),
    Novokuznetsk(28),
    Kemerovo(29),
    Ryazan(30),
    Tomsk(31),
    Astrakhan(32),
    Penza(33),
    Naberezhnye_Chelny(34),
    Lipetsk(35),
    Tula(36),
    Kirov(37),
    Cheboksary(38),
    Kaliningrad(39),
    Bryansk(40),
    Kursk(41),
    Ivanovo(42),
    Magnitogorsk(43),
    Ulan_Ude(44),
    Tver(45),
    Stavropol(46),
    Nizhny_Tagil(47),
    Belgorod(48),
    Arkhangelsk(49);

    private final int id;

    Cities(int code) {
        id = code;
    }
    public int getId()
    {
        return id;
    }
    public static Cities fromId(int code)
    {
        Cities[] list = Cities.values();

        if (code >= 0 && code < list.length)
            return list[code];
        else
            return Cities.Moscow;
    }

    }

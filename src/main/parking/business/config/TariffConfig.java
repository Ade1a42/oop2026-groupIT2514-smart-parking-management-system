package parking.business.config;

public class TariffConfig {
    private static TariffConfig instance;

    private double hourlyRate = 5.0;
    private double dailyMax = 100.0;

    private TariffConfig() {}

    public static TariffConfig getInstance() {
        if (instance == null) {
            instance = new TariffConfig();
        }
        return instance;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getDailyMax() {
        return dailyMax;
    }
}

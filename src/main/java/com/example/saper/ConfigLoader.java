package com.example.saper;

import java.io.*;
import java.util.Properties;

/**
 * Класс, загружающий конфигурационные данные из внешнего файла.
 */
public class ConfigLoader {

    /**
     * Метод загружает данные внешнего файла в
     * объект {@link Config} и возвращает его.
     * @param difficulty Игровая сложность.
     * @return Объект типа {@link Config}.
     */
    public static Config loadConfig(GameDifficulty difficulty) {

        InputStream inputStream = null;
        switch (difficulty) {
            case Easy -> inputStream = ConfigLoader.class.getResourceAsStream("/_config/easy_config.txt");
            case Normal -> inputStream = ConfigLoader.class.getResourceAsStream("/_config/normal_config.txt");
            case Hard -> inputStream = ConfigLoader.class.getResourceAsStream("/_config/hard_config.txt");
        }

        Config config = null;

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            Properties properties = new Properties();
            properties.load(reader);

            int countTile = Integer.parseInt(properties.getProperty("CountTile"));
            int sizeTile = Integer.parseInt(properties.getProperty("SizeTile"));
            int countMines = Integer.parseInt(properties.getProperty("CountMines"));
            String styleName = properties.getProperty("StyleName");

            config = new Config(countTile, sizeTile, countMines, styleName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }
}

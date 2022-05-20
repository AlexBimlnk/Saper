package com.example.saper;

/**
 * Перечисление, представляющее игровую сложноость.
 */
public enum GameDifficulty {
    Easy{
        public Config getConfigField(){
            return ConfigLoader.loadConfig(GameDifficulty.Easy);
        }
    },
    Normal{
        public Config getConfigField() {
            return ConfigLoader.loadConfig(GameDifficulty.Normal);
        }
    },
    Hard{
        public Config getConfigField() {
            return ConfigLoader.loadConfig(GameDifficulty.Hard);
        }
    };

    /**
     * Возвращает конфигурацию соотвественно игровой сложности.
     * @return Объект типа {@link Config}.
     */
    public abstract Config getConfigField();
}

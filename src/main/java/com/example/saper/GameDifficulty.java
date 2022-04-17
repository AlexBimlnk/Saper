package com.example.saper;

/**
 * Перечисление, представляющее игровую сложноость.
 */
public enum GameDifficulty {
    Easy{
        public Config GetConfigField(){
            return ConfigLoader.LoadConfig(GameDifficulty.Easy);
        }
    },
    Normal{
        public Config GetConfigField() {
            return ConfigLoader.LoadConfig(GameDifficulty.Normal);
        }
    },
    Hard{
        public Config GetConfigField() {
            return ConfigLoader.LoadConfig(GameDifficulty.Hard);
        }
    };

    /**
     * Возвращает конфигурацию соотвественно игровой сложности.
     * @return Объект типа {@link Config}.
     */
    public abstract Config GetConfigField();
}

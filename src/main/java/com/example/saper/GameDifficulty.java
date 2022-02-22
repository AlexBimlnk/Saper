package com.example.saper;

public enum GameDifficulty {
    Easy{
        public int GetCountField() { return 25; }
    },
    Normal{
        public int GetCountField() { return 50; }
    },
    Hard{
        public int GetCountField() { return 100; }
    };

    public  abstract int GetCountField();
}

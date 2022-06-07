package com.example.saper.gamefield;

import com.example.saper.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import java.security.InvalidParameterException;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Класс представляющий сущность игровой клетки.
 */
public class Tile extends Button {
    private static int _size;
    private final static Random rnd = (SaperApplication.getSeed() != -1 ? new Random(SaperApplication.getSeed()) : new Random());

    private int _minesAround; //quantity of mines around
    private final int _rowIndex;
    private final int _columIndex;

    private static BiConsumer<Integer, Integer> _callHandler; //используется при нажатии по пустой клетке
    private static Runnable _explosionEventHandler; //вызывается при взрыве мины
    public final Runnable TextView;

    private final BooleanProperty _clicked;
    private final BooleanProperty _flag;

    private boolean _isTwoButtonPressed = false;
    private boolean _isTwoButtonPressedHandler = true;

    public boolean isMine = false; //prop

    /**
     * Конструктор клетки.
     * @param rowIndex Координата клетки в строке.
     * @param columnIndex Координата клетки в столбце.
     */
    public Tile(int rowIndex, int columnIndex) throws IndexOutOfBoundsException {
        if (rowIndex < 0 || columnIndex < 0)
            throw new IndexOutOfBoundsException("Indexes can't be less than zero.");

        _clicked = new SimpleBooleanProperty(false);
        _clicked.addListener( e -> pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"),_clicked.get()));
        _clicked.addListener(GameController.clickListener);
        _flag = new SimpleBooleanProperty(false);
        _flag.addListener( e -> pseudoClassStateChanged(PseudoClass.getPseudoClass("flag"),_flag.get()));
        _flag.addListener(GameController.flagListener);

        loadDefaultSettings();
        _rowIndex = rowIndex;
        _columIndex = columnIndex;

        if (GameController.getGameDifficulty() == GameDifficulty.Hard) {
            TextView = (rnd.nextBoolean() ? this::showTextHard : this::showTextSimple);
        }
        else {
            TextView = this::showTextSimple;
        }
    }

    private void loadDefaultSettings() {
        setMinSize(_size, _size);
        setMaxSize(_size, _size);
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            _isTwoButtonPressed = event.isPrimaryButtonDown() && event.isSecondaryButtonDown();
            _isTwoButtonPressedHandler = _isTwoButtonPressed;
        });
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(_isTwoButtonPressed) {
                if(isClicked() && _isTwoButtonPressedHandler &&
                   GameController.getGameDifficulty() != GameDifficulty.Hard) {

                    int countMines = Field.countMinesAround(_rowIndex, _columIndex);
                    class Container {
                        public int countFlags = 0;
                    }
                    Container container = new Container();
                    
                    Field.applyToAround(_rowIndex, _columIndex, (coordinate) -> {
                        Tile tile = Field.getTile(coordinate.getKey(), coordinate.getValue());
                        if (tile.isFlag())
                            container.countFlags++;
                    }, 1);
                    
                    if (countMines == container.countFlags) {
                        _callHandler.accept(_rowIndex, _columIndex);
                    }

                    _isTwoButtonPressedHandler = false;
                }
            }
            else{
                if (isClicked()) {
                    return;
                }
                else if (event.getButton() == MouseButton.PRIMARY){
                    setClicked(true);
                }

                mouseHandler(event.getButton());
            }
        });


    }
    private void showTextSimple() {
        if (_minesAround == 0)
            return;

        setText(Integer.toString(_minesAround));

        if(_minesAround == 1)
            setTextFill(Color.BLUE);
        else if(_minesAround == 2)
            setTextFill(Color.GREEN);
        else if(_minesAround == 3)
            setTextFill(Color.RED);
        else if(_minesAround == 4)
            setTextFill(Color.PURPLE);
        else if(_minesAround == 5)
            setTextFill(Color.ORANGE);
        else if(_minesAround == 6)
            setTextFill(Color.YELLOW);
        else if(_minesAround == 7)
            setTextFill(Color.PINK);
        else if(_minesAround == 8)
            setTextFill(Color.BLACK);
    }
    private void showTextHard() {
        if (_minesAround == 0)
            return;

        setTextFill(Color.BLACK);
        int lowerBound = _minesAround - rnd.nextInt(3);
        int upperBound = _minesAround + rnd.nextInt(3);

        if(lowerBound < 0)
            lowerBound = 0;

        if(upperBound > 8)
            upperBound = 8;

        if(upperBound != lowerBound)
            setText(lowerBound + "-" + upperBound);
        else
            showTextSimple();
    }

    /**
     * Обработчик события нажатия мыши по кнопке.
     * @param button Нажатая кнопка.
     */
    public void mouseHandler(MouseButton button) {
        //Если нажали на ЛКМ
        if(button == MouseButton.PRIMARY && !isFlag()) {
            setClicked(true);
            if (isMine) {
                this.setId("mine");
                if(_explosionEventHandler != null) {
                    _explosionEventHandler.run();
                }
            }
            else {
                if (getMinesAround() == 0) {
                    if (_callHandler != null) {
                        _callHandler.accept(_rowIndex,_columIndex);
                    }
                }
                else {
                    TextView.run();
                }
            }
        }
        if(button == MouseButton.SECONDARY && GameController.getGameCondition()) {
            setFlag(!isFlag());
        }
    }

    /**
     * Метод устанавливает кол-во мин вокруг клетки.
     * @param value Кол-во мин.
     */
    public void setMinesAround(int value) throws InvalidParameterException {
        if (value < 0)
            throw new InvalidParameterException();
        _minesAround = value;
    }

    /**
     * Метод получает кол-во мин вокруг клетки.
     * @return Кол-во мин.
     */
    public int getMinesAround() {
        return _minesAround;
    }

    /**
     * Метод устанавливает размер клетки.
     * @param size Размер.
     */
    public static void setSize(int size) throws InvalidParameterException {
        if (size <= 0)
            throw new InvalidParameterException("Size should be positive.");

        _size = size;
    }

    /**
     * Показывает, была ли открыта клетка или нет.
     * @return true, если клетка уже была открыта, иначе - false
     */
    public boolean isClicked() {
        return _clicked.get();
    }

    /**
     * Устанавливает статус открытия клетки.
     * @param clicked Статус.
     */
    public void setClicked(boolean clicked) {
        if (!isFlag()) {
            _clicked.set(clicked);
        }
    }

    /**
     * Метод проверяет устанавку флага на клетку.
     * @return true, если клетка помечена флагом, иначе - false
     */
    public boolean isFlag() {
        return _flag.get();
    }

    /**
     * Устанавливает статус, отражающий положение установки флага.
     * @param flag Статус.
     */
    public void setFlag(boolean flag) {
        if (!isClicked()) {
            this._flag.set(flag);
        }
    }

    /**
     * Устанавливает делегат, который следует вызвать при открытии соседних клеток.
     * @param call делегат
     */
    public static void setCall(BiConsumer<Integer, Integer> call) {
        _callHandler = call;
    }
    /**
     * Устанавливает действие, которое вызовется при взрыве, если клетка оказалась миной.
     * @param explosion Действие, которое следует применить.
     */
    public static void setExplosionEvent(Runnable explosion) {
        _explosionEventHandler = explosion;
    }
}
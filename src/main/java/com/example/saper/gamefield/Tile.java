package com.example.saper.gamefield;

import com.example.saper.GameController;
import com.example.saper.GameDifficulty;
import com.example.saper.SaperApplication;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.security.InvalidParameterException;
import java.util.Random;

/**
 * Класс представляющий сущность игровой клетки.
 */
public class Tile extends Button {
    private static int _size;
    private final static Random rnd = (SaperApplication.getSeed() != -1 ? new Random(SaperApplication.getSeed()) : new Random());

    private int _minesAround; //quantity of mines around
    private final int _rowIndex;
    private final int _columIndex;

    private static CallNearby _callHandler; //используется при нажатии по пустой клетке
    private static ExplosionEvent _explosionEventHandler; //вызывается при взрыве мины

    private final BooleanProperty _clicked;
    private final BooleanProperty _flag;

    private boolean _isTwoButtonPressed = false;
    private boolean _isTwoButtonPressedHandler = true;

    public final ShownTextHandler TextView;

    /**
     * Конструктор клетки.
     * @param rowIndex Координата клетки в строке.
     * @param columnIndex Координата клетки в столбце.
     */
    public Tile(int rowIndex, int columnIndex){
        _clicked = new SimpleBooleanProperty(false);
        _clicked.addListener( e -> pseudoClassStateChanged(PseudoClass.getPseudoClass("clicked"),_clicked.get()));
        _clicked.addListener(GameController.clickListener);

        _flag = new SimpleBooleanProperty(false);
        _flag.addListener( e -> pseudoClassStateChanged(PseudoClass.getPseudoClass("flag"),_flag.get()));
        _flag.addListener(GameController.flagListener);

        LoadDefaultSettings();
        _rowIndex = rowIndex;
        _columIndex = columnIndex;

        if (GameController.getGameDifficulty() == GameDifficulty.Hard) {
            TextView = (rnd.nextBoolean() ? this::ShowTextHard : this::ShowTextSimple);
        }
        else {
            TextView = this::ShowTextSimple;
        }
    }

    public boolean IsMine = false; //prop

    private void LoadDefaultSettings() {

        setMinSize(_size, _size);
        setMaxSize(_size, _size);
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            _isTwoButtonPressed = event.isPrimaryButtonDown() && event.isSecondaryButtonDown();
            _isTwoButtonPressedHandler = _isTwoButtonPressed;
        });
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            //Todo нажатие сразу на две кнопки

            if(_isTwoButtonPressed){
                if(isClicked()){
                    //К сожалению при двойном щелчке он войдет в эту секцию два раза
                    //Так как нажаты и левая и правая, у них обоих сработает кликед.
                    //Таким образом этот блок кода вызовется два раза
                    //Решение - вставить ещё одну проверку сюда через буль. Костыль но
                    //Если сетить _isTwoButtonPressed = false после выхода первого срабатывания
                    //Хоть второй раз он и не зайдет, но отработает нажатие на коде ниже
                    //Т.е вызовется блок else.
                    //В связи с чем если даблкликать по закрытым клеткам они будут или открываться
                    //Или "флаггироваться"
                    //Сейчас же этот участок как и положено срабатывает только на уже открытых клетках
                    //Но два раза. Как я уже говорил, после перового срабатывания нужно сетить доп свойство
                    //Костыль сделал ниже:
                    if(_isTwoButtonPressedHandler){
                        int i = 123; //logic

                        _isTwoButtonPressedHandler = false;
                    }
                }
            }
            else{
                if (isClicked()) {
                    return;
                }
                else if (event.getButton() == MouseButton.PRIMARY){
                    setClicked(true);
                }

                MouseHandler(event.getButton());
            }
        });


    }
    private void ShowTextSimple() {
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
    private void ShowTextHard() {
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
            ShowTextSimple();
    }

    /**
     * Обработчик события нажатия мыши по кнопке.
     * @param button Нажатая кнопка.
     */
    public void MouseHandler(MouseButton button) {
        //Если нажали на ЛКМ
        if(button == MouseButton.PRIMARY && !isFlag()) {
            if (IsMine) {
                this.setId("mine");
                if(_explosionEventHandler != null) {
                    _explosionEventHandler.Invoke();
                }
            }
            else {
                setClicked(true);
                if (getMinesAround() == 0) {
                    if (_callHandler != null) {
                        _callHandler.Invoke(_rowIndex,_columIndex);
                    }
                }
                else {
                    TextView.Invoke();
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
        if(value < 0)
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
    public static void setSize(int size) {
        _size = size;
    }

    /**
     * Устанавливает действие, которое вызовется при взрыве, если клетка оказалась миной.
     * @param explosion Действие, которое следует применить.
     */
    public static void setExplosionEvent(ExplosionEvent explosion) {
        _explosionEventHandler = explosion;
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
     * Делегат, вызывающийся при открытии соседних клеток.
     */
    public interface CallNearby {
        void Invoke(int i,int y);
    }

    /**
     * Устанавливает делегат, который следует вызвать при открытии соседних клеток.
     * @param call
     */
    public static void setCall(CallNearby call) {
        _callHandler = call;
    }

    /**
     * Делегат, вызывающийся при взрыве.
     */
    public interface ExplosionEvent {
        void Invoke();
    }

    /**
     * Делегат, вызывающийся при отображении текста на кнопке.
     */
    public interface ShownTextHandler {
        void Invoke();
    }
}
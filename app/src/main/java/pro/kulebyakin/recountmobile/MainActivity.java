package pro.kulebyakin.recountmobile;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextInputLayout whiteSalInputTil;
    TextInputLayout blackSalInputTil;
    TextInputLayout weekendsInputTil;
    TextInputLayout mileageInputTil;
    TextInputLayout fuelCostInputTil;
    TextInputLayout consumptionInputTil;
    TextInputEditText whiteSalInput;
    TextInputEditText blackSalInput;
    TextInputEditText weekendsInput;
    TextInputEditText mileageInput;
    TextInputEditText fuelCostInput;
    TextInputEditText consumptionInput;
    TextView result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Запрет смены ориентации
        //setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        whiteSalInputTil = findViewById(R.id.whiteSalaryTil);
        blackSalInputTil = findViewById(R.id.blackSalaryTil);
        weekendsInputTil = findViewById(R.id.weekendsTil);
        mileageInputTil = findViewById(R.id.mileageTil);
        fuelCostInputTil = findViewById(R.id.fuelCostTil);
        consumptionInputTil = findViewById(R.id.consumptionTil);
        whiteSalInput = whiteSalInputTil.findViewById(R.id.whiteSalary);
        blackSalInput = blackSalInputTil.findViewById(R.id.blackSalary);
        weekendsInput = weekendsInputTil.findViewById(R.id.weekends);
        mileageInput = mileageInputTil.findViewById(R.id.mileage);
        fuelCostInput = fuelCostInputTil.findViewById(R.id.fuelCost);
        consumptionInput = consumptionInputTil.findViewById(R.id.consumption);
        result = findViewById(R.id.result);

        consumptionInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    printResult();
                    return true;
                }
                return false;
            }
        });

        Button resBtn = findViewById(R.id.printResult);
        resBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printResult();
            }
        });
    }

    double getValue(EditText input) {
        try {
            return Double.parseDouble(input.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void printResult() {
        double tax = 0.87; //Налог 13%
        double valPayToWeekend = 2500; //Оплата выходного дня
        double valByKM = 8.5; //Оплата амортизации

        double whiteSal = getValue(whiteSalInput);
        double blackSal = getValue(blackSalInput);
        double weekends = getValue(weekendsInput);
        double mileage = getValue(mileageInput);
        double fuelCost = getValue(fuelCostInput);
        double consumption = getValue(consumptionInput);

        //Расчет топлива, затрат на топливо
        double allLit = (mileage/100.0) * consumption;
        double allMoneyToLit = allLit * fuelCost;

        //Расчет выходных дней
        double allPay = whiteSal * tax + blackSal;
        double allPayByWeekend = weekends * valPayToWeekend;

        //Расчет амортизации
        double allPayByMileage = mileage * valByKM;

        //Итого
        double all = allPay + allPayByWeekend + allPayByMileage;

        //Итого без затрат на бензин
        double allClear = all - allMoneyToLit;

        result.setText("Израсходовано топлива: " + (int)allLit + " литров\n" +
        "Затраты на топливо: " + (int)allMoneyToLit + " рублей\n" +
        "Вы получите: " + (int)all + " рублей\n" +
        "Без учёта топлива: " + (int)allClear + " рублей");

        // Скрыть клавиатуру
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus();
        // findViewById(R.id.result).requestFocus();
    }


    public void clearAll(View view) {
        whiteSalInput.setText(null);
        blackSalInput.setText(null);
        weekendsInput.setText(null);
        mileageInput.setText(null);
        fuelCostInput.setText(null);
        consumptionInput.setText(null);
        result.setText(null);

        // Скрыть клавиатуру
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus();
    }

    // Магия. Сброс фокуса при клике вне EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}

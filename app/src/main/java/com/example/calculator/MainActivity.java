package com.example.calculator;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * MainActivity class represents the main screen of the calculator application.
 * This activity handles user input for arithmetic operations, displays the input,
 * computes results, and handles the display and button functionality.
 *
 * @author Daniel Tongu
 */
public class MainActivity extends AppCompatActivity {

    // Max and min text sizes for the result
    private final float MAX_TEXT_SIZE = 90f;
    private final float MIN_TEXT_SIZE = 20f;
    private final StringBuilder INPUT = new StringBuilder();  // Holds the user's input for the expression
    private boolean lastInputIsOperator = false;  // Prevents entering two consecutive operators
    private boolean lastResultDisplayed = false;  // Tracks if the result is being displayed
    private TextView computationDisplay;  // Displays the expression
    private TextView resultDisplay;       // Displays the result


    /**
     * Initializes the activity, sets up the layout, button click listeners, and
     * initializes the display TextViews.
     *
     * @param savedInstanceState The saved state of the app instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the display TextViews
        computationDisplay = findViewById(R.id.computation_display);
        resultDisplay = findViewById(R.id.result_display);

        // Set the max text size for the result display (initial size)
        resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, MAX_TEXT_SIZE);

        // Set up number button click listeners (buttons 0-9)
        setNumberButtonClickListener(R.id.button0, "0");
        setNumberButtonClickListener(R.id.button1, "1");
        setNumberButtonClickListener(R.id.button2, "2");
        setNumberButtonClickListener(R.id.button3, "3");
        setNumberButtonClickListener(R.id.button4, "4");
        setNumberButtonClickListener(R.id.button5, "5");
        setNumberButtonClickListener(R.id.button6, "6");
        setNumberButtonClickListener(R.id.button7, "7");
        setNumberButtonClickListener(R.id.button8, "8");
        setNumberButtonClickListener(R.id.button9, "9");

        // Set up operator button click listeners
        findViewById(R.id.buttonAdd).setOnClickListener(v -> appendOperator("+"));
        findViewById(R.id.buttonSubtract).setOnClickListener(v -> appendOperator("-"));
        findViewById(R.id.buttonMultiply).setOnClickListener(v -> appendOperator("*"));
        findViewById(R.id.buttonDivide).setOnClickListener(v -> appendOperator("/"));
        findViewById(R.id.buttonOpenBracket).setOnClickListener(v -> appendToComputation("("));
        findViewById(R.id.buttonCloseBracket).setOnClickListener(v -> appendToComputation(")"));
        findViewById(R.id.buttonDecimal).setOnClickListener(v -> appendToComputation("."));

        // Handle equals button
        findViewById(R.id.buttonEquals).setOnClickListener(v -> calculateResult());

        // Clear buttons functionality
        findViewById(R.id.buttonAC).setOnClickListener(v -> {
            INPUT.setLength(0);  // Clear input
            computationDisplay.setText("");  // Clear computation display
            resultDisplay.setText("0");  // Reset result to 0
            lastResultDisplayed = false;  // Reset flag
            // Adjust text size to fit result in one line
            adjustTextSizeToFit(resultDisplay);
        });

        // Clear last entry button (C)
        findViewById(R.id.buttonC).setOnClickListener(v -> {
            if (INPUT.length() > 0) {
                INPUT.deleteCharAt(INPUT.length() - 1);  // Remove the last character
                computationDisplay.setText(INPUT.toString());  // Update display
            }
        });
    }

    /**
     * Helper function to set up number button click listeners.
     * Attaches a listener to a button and appends the corresponding number to the computation display.
     *
     * @param buttonId The resource ID of the button (e.g., R.id.button0).
     * @param value The number to append when the button is clicked.
     */
    private void setNumberButtonClickListener(int buttonId, String value) {
        findViewById(buttonId).setOnClickListener(v -> appendToComputation(value));
    }

    /**
     * Appends numbers and decimal points to the computation display.
     * If a result was just displayed, the new input starts a new expression.
     *
     * @param value The number or decimal point to append to the display.
     */
    private void appendToComputation(String value) {
        if (lastResultDisplayed) {
            INPUT.setLength(0);  // Clear the input if result was displayed
            lastResultDisplayed = false;
        }
        INPUT.append(value);
        computationDisplay.setText(INPUT.toString());  // Update computation display
        lastInputIsOperator = false;  // Reset operator flag
    }

    /**
     * Appends operators to the computation display while preventing double operators.
     * If the last input was a result, starts a new operation using that result.
     *
     * @param operator The operator to append to the display (e.g., +, -, *, /).
     */
    private void appendOperator(String operator) {
        if (INPUT.length() > 0 && !lastInputIsOperator) {
            if (lastResultDisplayed) {
                INPUT.setLength(0);  // Clear input to start new expression after result
                INPUT.append(resultDisplay.getText());  // Use result for next operation
                lastResultDisplayed = false;
            }
            INPUT.append(operator);
            computationDisplay.setText(INPUT.toString());
            lastInputIsOperator = true;  // Mark the last input as an operator
        }
    }

    /**
     * Calculates the result of the expression currently in the computation display.
     * Shrinks the text size of the result if it's too large to fit on one line.
     */
    private void calculateResult() {
        try {
            // Parse the input string as a mathematical expression using exp4j
            String expression = INPUT.toString();
            Expression exp = new ExpressionBuilder(expression).build();
            double result = exp.evaluate();  // Evaluate the expression
            resultDisplay.setText(String.valueOf(result));  // Display the result
            lastResultDisplayed = true;  // Mark that the result is being displayed
        } catch (Exception e) {
            resultDisplay.setText("Error");  // Handle invalid expression
            lastResultDisplayed = false;
        }
        // Adjust text size to fit result in one line
        adjustTextSizeToFit(resultDisplay);
    }

    /**
     * Dynamically adjusts the text size of the resultDisplay to fit within the width of the TextView.
     * Ensures the text is always right-justified.
     *
     * @param textView The TextView containing the result to be resized and aligned.
     */
    private void adjustTextSizeToFit(TextView textView) {
        // Set the text to be right-aligned
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // For Android versions above Jelly Bean MR1, use setTextAlignment
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END); // Ensures right alignment
        } else {
            // For older Android versions, use gravity
            textView.setGravity(Gravity.RIGHT);
        }

        int viewWidth = textView.getWidth(); // Get the available width of the TextView
        String text = textView.getText().toString(); // Get the text to be displayed

        // Start with the maximum text size
        float textSize = MAX_TEXT_SIZE;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        // Measure the width of the text with the current text size
        textView.measure(0, 0);
        float textWidth = textView.getPaint().measureText(text);

        // Reduce the text size until it fits within the TextView's width or hits the minimum size
        while (textWidth > viewWidth && textSize > MIN_TEXT_SIZE) {
            textSize -= 1; // Decrease text size by 1sp
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textWidth = textView.getPaint().measureText(text); // Recalculate text width
        }
    }
}
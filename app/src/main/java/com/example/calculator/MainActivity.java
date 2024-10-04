package com.example.calculator;

import android.os.Bundle;
import android.util.TypedValue;
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

    private TextView computationDisplay;  // Displays the expression
    private TextView resultDisplay;       // Displays the result
    private StringBuilder input = new StringBuilder();  // Holds the user's input for the expression
    private boolean lastInputIsOperator = false;  // Prevents entering two consecutive operators
    private boolean lastResultDisplayed = false;  // Tracks if the result is being displayed

    // Max and min text sizes for the result
    private final float MAX_TEXT_SIZE = 60f;
    private final float MIN_TEXT_SIZE = 20f;

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

        // Handle special buttons
        findViewById(R.id.buttonToggleSign).setOnClickListener(v -> toggleSign());
        findViewById(R.id.buttonPercentage).setOnClickListener(v -> applyPercentage());

        // Clear buttons functionality
        findViewById(R.id.buttonAC).setOnClickListener(v -> {
            input.setLength(0);  // Clear input
            computationDisplay.setText("");  // Clear computation display
            resultDisplay.setText("0");  // Reset result to 0
            lastResultDisplayed = false;  // Reset flag
            resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, MAX_TEXT_SIZE);  // Reset text size
        });

        // Clear last entry button (C)
        findViewById(R.id.buttonC).setOnClickListener(v -> {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);  // Remove the last character
                computationDisplay.setText(input.toString());  // Update display
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
            input.setLength(0);  // Clear the input if result was displayed
            lastResultDisplayed = false;
        }
        input.append(value);
        computationDisplay.setText(input.toString());  // Update computation display
        lastInputIsOperator = false;  // Reset operator flag
    }

    /**
     * Appends operators to the computation display while preventing double operators.
     * If the last input was a result, starts a new operation using that result.
     *
     * @param operator The operator to append to the display (e.g., +, -, *, /).
     */
    private void appendOperator(String operator) {
        if (input.length() > 0 && !lastInputIsOperator) {
            if (lastResultDisplayed) {
                input.setLength(0);  // Clear input to start new expression after result
                input.append(resultDisplay.getText());  // Use result for next operation
                lastResultDisplayed = false;
            }
            input.append(operator);
            computationDisplay.setText(input.toString());
            lastInputIsOperator = true;  // Mark the last input as an operator
        }
    }

    /**
     * Toggles the sign of the current number in the computation (i.e., positive to negative or vice versa).
     */
    private void toggleSign() {
        if (input.length() > 0 && !lastInputIsOperator) {
            try {
                // Get the last entered number, negate it, and update the input
                String currentInput = input.toString();
                double value = Double.parseDouble(currentInput);
                value = value * -1;
                input.setLength(0);  // Clear the input
                input.append(value);  // Add the negated value
                computationDisplay.setText(input.toString());
            } catch (NumberFormatException e) {
                // Handle invalid input: Display error message
                computationDisplay.setText("Error");
            }
        }
    }

    /**
     * Converts the current number into its percentage value by dividing it by 100.
     */
    private void applyPercentage() {
        if (input.length() > 0 && !lastInputIsOperator) {
            try {
                // Parse the current input as a double and divide it by 100
                String currentInput = input.toString();
                double value = Double.parseDouble(currentInput);
                value = value / 100;
                input.setLength(0);  // Clear the input
                input.append(value);  // Append the percentage result
                computationDisplay.setText(input.toString());
            } catch (NumberFormatException e) {
                // Handle invalid input: Display error message
                computationDisplay.setText("Error");
            }
        }
    }

    /**
     * Calculates the result of the expression currently in the computation display.
     * Shrinks the text size of the result if it's too large to fit on one line.
     */
    private void calculateResult() {
        try {
            // Parse the input string as a mathematical expression using exp4j
            String expression = input.toString();
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
     * Dynamically adjusts the text size of the result display to fit within the width of the TextView.
     *
     * @param textView The TextView containing the result to be resized.
     */
    private void adjustTextSizeToFit(TextView textView) {
        int viewWidth = textView.getWidth();
        String text = textView.getText().toString();

        // Start with the maximum text size
        float textSize = MAX_TEXT_SIZE;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        // Measure the width of the text
        textView.measure(0, 0);
        float textWidth = textView.getPaint().measureText(text);

        // Reduce the text size until it fits within the TextView's width or hits the minimum size
        while (textWidth > viewWidth && textSize > MIN_TEXT_SIZE) {
            textSize -= 1;  // Reduce text size
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textWidth = textView.getPaint().measureText(text);  // Recalculate text width
        }
    }
}
package com.minagic.minagic.testing.spells;

import com.minagic.minagic.utilities.PowerCalibrator;

import java.util.Locale;

public class CalibratorTest {
    public static void main(String[] args) {
        PowerCalibrator calibrator = PowerCalibrator.of(new PowerCalibrator.ExponentialCurve(2f))
                .flatlineBelow(0.2f)
                .flatlineAbove(0.9f)
                .invert()
                .remap(5f, 25f);  // Final output range

        float step = 0.001f;
        for (float input = 0f; input <= 1.001f; input += step) {
            float output = calibrator.apply(input);
            System.out.printf(Locale.US, "%.3f -> %.3f%n", input, output);
        }
    }
}

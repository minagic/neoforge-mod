package com.minagic.minagic.utilities;

public final class PowerCalibrator {

    public static interface PowerCurve {
        public float apply(float value);
    }

    private PowerCurve curve;
    private boolean inverted = false;
    private float flatMin = 0f;
    private float flatMax = 1f;
    private float outMin = 0f;
    private float outMax = 1f;

    private PowerCalibrator(PowerCurve curve) {
        this.curve = curve;
    }

    public static PowerCalibrator of(PowerCurve curve) {
        return new PowerCalibrator(curve);
    }

    public PowerCalibrator invert() {
        this.inverted = true;
        return this;
    }

    public PowerCalibrator flatlineBelow(float x1) {
        this.flatMin = x1;
        return this;
    }

    public PowerCalibrator flatlineAbove(float x2) {
        this.flatMax = x2;
        return this;
    }

    public PowerCalibrator remap(float newMin, float newMax) {
        this.outMin = newMin;
        this.outMax = newMax;
        return this;
    }

    public float apply(float input) {
        if (input < flatMin) return inverted ? outMax : outMin;
        if (input > flatMax) return inverted ? outMin : outMax;

        float adjusted = (input - flatMin) / (flatMax - flatMin);
        float value = curve.apply(adjusted);
        if (inverted) value = 1f - value;

        return outMin + value * (outMax - outMin);
    }


    public static class LinearCurve implements PowerCurve {
        public static final LinearCurve INSTANCE = new LinearCurve();
        public float apply(float input) { return input; }
    }

    public static class SinusoidalCurve implements PowerCurve {
        public static final SinusoidalCurve INSTANCE = new SinusoidalCurve();
        public float apply(float input) {
            return (float) Math.sin(input * Math.PI * 0.5); // rises fast, then slows
        }
    }

    public static class ExponentialCurve implements PowerCurve {
        private final float exponent;
        public ExponentialCurve(float exponent) {
            this.exponent = exponent;
        }
        public float apply(float input) {
            return (float) Math.pow(input, exponent);
        }
    }
}
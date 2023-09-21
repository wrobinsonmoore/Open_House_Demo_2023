package com.SLU_multimodal_touch.Graphs;

import android.os.SystemClock;
import android.view.MotionEvent;

public class GestureAnalyser {
    public static final boolean DEBUG = true;
    public static final int SWIPE_1_UP = 11;
    public static final int SWIPE_1_DOWN = 12;
    public static final int SWIPE_1_LEFT = 13;
    public static final int SWIPE_1_RIGHT = 14;
    public static final int SWIPE_2_UP = 21;
    public static final int SWIPE_2_DOWN = 22;
    public static final int SWIPE_2_LEFT = 23;
    public static final int SWIPE_2_RIGHT = 24;
    public static final int SWIPE_3_UP = 31;
    public static final int SWIPE_3_DOWN = 32;
    public static final int SWIPE_3_LEFT = 33;
    public static final int SWIPE_3_RIGHT = 34;
    public static final int SWIPE_4_UP = 41;
    public static final int SWIPE_4_DOWN = 42;
    public static final int SWIPE_4_LEFT = 43;
    public static final int SWIPE_4_RIGHT = 44;
    public static final int PINCH_2 = 25;
    public static final int UNPINCH_2 = 26;
    public static final int PINCH_3 = 35;
    public static final int UNPINCH_3 = 36;
    public static final int PINCH_4 = 45;
    public static final int UNPINCH_4 = 46;
    public static final int DOUBLE_TAP_1 = 107;
    public static final int SWIPING_1_UP = 101;
    public static final int SWIPING_1_DOWN = 102;
    public static final int SWIPING_1_LEFT = 103;
    public static final int SWIPING_1_RIGHT = 104;
    public static final int SWIPING_2_UP = 201;
    public static final int SWIPING_2_DOWN = 202;
    public static final int SWIPING_2_LEFT = 203;
    public static final int SWIPING_2_RIGHT = 204;
    public static final int PINCHING = 205;
    public static final int UNPINCHING = 206;
    private static final String TAG = "GestureAnalyser";
    private double[] initialX;
    private double[] initialY;
    private double[] finalX;
    private double[] finalY;
    private double[] currentX;
    private double[] currentY;
    private double[] delX;
    private double[] delY;
    private int numFingers;
    private long initialT;
    private long finalT;
    private long currentT;
    private long prevInitialT;
    private long prevFinalT;
    private int swipeSlopeIntolerance;
    private long doubleTapMaxDelayMillis;
    private long doubleTapMaxDownMillis;

    public GestureAnalyser() {
        this(3, 500, 100);
    }

    public GestureAnalyser(int swipeSlopeIntolerance, int doubleTapMaxDelayMillis, int doubleTapMaxDownMillis) {
        this.initialX = new double[5];
        this.initialY = new double[5];
        this.finalX = new double[5];
        this.finalY = new double[5];
        this.currentX = new double[5];
        this.currentY = new double[5];
        this.delX = new double[5];
        this.delY = new double[5];
        this.numFingers = 0;
        this.swipeSlopeIntolerance = 3;
        this.swipeSlopeIntolerance = swipeSlopeIntolerance;
        this.doubleTapMaxDownMillis = (long)doubleTapMaxDownMillis;
        this.doubleTapMaxDelayMillis = (long)doubleTapMaxDelayMillis;
    }

    public void trackGesture(MotionEvent ev) {
        int n = ev.getPointerCount();

        for(int i = 0; i < n; ++i) {
            this.initialX[i] = (double)ev.getX(i);
            this.initialY[i] = (double)ev.getY(i);
        }

        this.numFingers = n;
        this.initialT = SystemClock.uptimeMillis();
    }

    public void untrackGesture() {
        this.numFingers = 0;
        this.prevFinalT = SystemClock.uptimeMillis();
        this.prevInitialT = this.initialT;
    }

    public GestureType getGesture(MotionEvent ev) {
        double averageDistance = 0.0;

        for(int i = 0; i < this.numFingers; ++i) {
            this.finalX[i] = (double)ev.getX(i);
            this.finalY[i] = (double)ev.getY(i);
            this.delX[i] = this.finalX[i] - this.initialX[i];
            this.delY[i] = this.finalY[i] - this.initialY[i];
            averageDistance += Math.sqrt(Math.pow(this.finalX[i] - this.initialX[i], 2.0) + Math.pow(this.finalY[i] - this.initialY[i], 2.0));
        }

        averageDistance /= (double)this.numFingers;
        this.finalT = SystemClock.uptimeMillis();
        GestureType gt = new GestureType();
        gt.setGestureFlag(this.calcGesture());
        gt.setGestureDuration(this.finalT - this.initialT);
        gt.setGestureDistance(averageDistance);
        return gt;
    }

    public int getOngoingGesture(MotionEvent ev) {
        for(int i = 0; i < this.numFingers; ++i) {
            this.currentX[i] = (double)ev.getX(i);
            this.currentY[i] = (double)ev.getY(i);
            this.delX[i] = this.finalX[i] - this.initialX[i];
            this.delY[i] = this.finalY[i] - this.initialY[i];
        }

        this.currentT = SystemClock.uptimeMillis();
        return this.calcGesture();
    }

    private int calcGesture() {
        if (this.isDoubleTap()) {
            return 107;
        } else {
            if (this.numFingers == 1) {
                if (-this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0])) {
                    return 11;
                }

                if (this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0])) {
                    return 12;
                }

                if (-this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0])) {
                    return 13;
                }

                if (this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0])) {
                    return 14;
                }
            }

            if (this.numFingers == 2) {
                if (-this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0]) && -this.delY[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[1])) {
                    return 21;
                }

                if (this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0]) && this.delY[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[1])) {
                    return 22;
                }

                if (-this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0]) && -this.delX[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[1])) {
                    return 23;
                }

                if (this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0]) && this.delX[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[1])) {
                    return 24;
                }

                if (this.finalFingDist(0, 1) > 2.0 * this.initialFingDist(0, 1)) {
                    return 26;
                }

                if (this.finalFingDist(0, 1) < 0.5 * this.initialFingDist(0, 1)) {
                    return 25;
                }
            }

            if (this.numFingers == 3) {
                if (-this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0]) && -this.delY[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[1]) && -this.delY[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[2])) {
                    return 31;
                }

                if (this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0]) && this.delY[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[1]) && this.delY[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[2])) {
                    return 32;
                }

                if (-this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0]) && -this.delX[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[1]) && -this.delX[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[2])) {
                    return 33;
                }

                if (this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0]) && this.delX[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[1]) && this.delX[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[2])) {
                    return 34;
                }

                if (this.finalFingDist(0, 1) > 1.75 * this.initialFingDist(0, 1) && this.finalFingDist(1, 2) > 1.75 * this.initialFingDist(1, 2) && this.finalFingDist(2, 0) > 1.75 * this.initialFingDist(2, 0)) {
                    return 36;
                }

                if (this.finalFingDist(0, 1) < 0.66 * this.initialFingDist(0, 1) && this.finalFingDist(1, 2) < 0.66 * this.initialFingDist(1, 2) && this.finalFingDist(2, 0) < 0.66 * this.initialFingDist(2, 0)) {
                    return 35;
                }
            }

            if (this.numFingers == 4) {
                if (-this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0]) && -this.delY[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[1]) && -this.delY[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[2]) && -this.delY[3] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[3])) {
                    return 41;
                }

                if (this.delY[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[0]) && this.delY[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[1]) && this.delY[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[2]) && this.delY[3] > (double)this.swipeSlopeIntolerance * Math.abs(this.delX[3])) {
                    return 42;
                }

                if (-this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0]) && -this.delX[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[1]) && -this.delX[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[2]) && -this.delX[3] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[3])) {
                    return 43;
                }

                if (this.delX[0] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[0]) && this.delX[1] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[1]) && this.delX[2] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[2]) && this.delX[3] > (double)this.swipeSlopeIntolerance * Math.abs(this.delY[3])) {
                    return 44;
                }

                if (this.finalFingDist(0, 1) > 1.5 * this.initialFingDist(0, 1) && this.finalFingDist(1, 2) > 1.5 * this.initialFingDist(1, 2) && this.finalFingDist(2, 3) > 1.5 * this.initialFingDist(2, 3) && this.finalFingDist(3, 0) > 1.5 * this.initialFingDist(3, 0)) {
                    return 46;
                }

                if (this.finalFingDist(0, 1) < 0.8 * this.initialFingDist(0, 1) && this.finalFingDist(1, 2) < 0.8 * this.initialFingDist(1, 2) && this.finalFingDist(2, 3) < 0.8 * this.initialFingDist(2, 3) && this.finalFingDist(3, 0) < 0.8 * this.initialFingDist(3, 0)) {
                    return 45;
                }
            }

            return 0;
        }
    }

    private double initialFingDist(int fingNum1, int fingNum2) {
        return Math.sqrt(Math.pow(this.initialX[fingNum1] - this.initialX[fingNum2], 2.0) + Math.pow(this.initialY[fingNum1] - this.initialY[fingNum2], 2.0));
    }

    private double finalFingDist(int fingNum1, int fingNum2) {
        return Math.sqrt(Math.pow(this.finalX[fingNum1] - this.finalX[fingNum2], 2.0) + Math.pow(this.finalY[fingNum1] - this.finalY[fingNum2], 2.0));
    }

    public boolean isDoubleTap() {
        return this.initialT - this.prevFinalT < this.doubleTapMaxDelayMillis && this.finalT - this.initialT < this.doubleTapMaxDownMillis && this.prevFinalT - this.prevInitialT < this.doubleTapMaxDownMillis;
    }

    public class GestureType {
        private int gestureFlag;
        private long gestureDuration;
        private double gestureDistance;

        public GestureType() {
        }

        public long getGestureDuration() {
            return this.gestureDuration;
        }

        public void setGestureDuration(long gestureDuration) {
            this.gestureDuration = gestureDuration;
        }

        public int getGestureFlag() {
            return this.gestureFlag;
        }

        public void setGestureFlag(int gestureFlag) {
            this.gestureFlag = gestureFlag;
        }

        public double getGestureDistance() {
            return this.gestureDistance;
        }

        public void setGestureDistance(double gestureDistance) {
            this.gestureDistance = gestureDistance;
        }
    }
}

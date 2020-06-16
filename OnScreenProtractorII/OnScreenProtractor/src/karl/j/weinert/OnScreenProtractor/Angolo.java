/*
 * OnScreenProtractor v0.5
 * =======================
 *
 * Copyright (C) 2016 Paolo Straffi <p_straffi@hotmail.com>
 * 
 * This file is part of OnScreenProtractor.
 *
 * OnScreenProtractor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * OnScreenProtractor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OnScreenProtractor.  If not, see <http://www.gnu.org/licenses/>.    
 */
package karl.j.weinert.OnScreenProtractor;

import java.awt.Point;

/**
 *
 * @author Paolo Straffi
 */
public class Angolo {

    private Point point0;
    private Point point1;
    private Point point2;
    private double correzione;

    public Angolo(Point point0, Point point1, Point point2, double correzione) {
        this.point0 = point0;
        this.point1 = point1;
        this.point2 = point2;
        this.correzione = correzione;
    }

    public Angolo(Point point0, Point point1, Point point2) {
        this.point0 = point0;
        this.point1 = point1;
        this.point2 = point2;
    }

    public Point getPoint0() {
        return point0;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public double getCorrezione() {
        return correzione;
    }

    public void setPoint0(Point point0) {
        this.point0 = point0;
    }

    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    public void setCorrezione(double correzione) {
        this.correzione = correzione;
    }

    public double valoreAngolo01() {

        double val01;
        int deltaX01;
        int deltaY01;

        // public static double atan2(double y, double x)
        // Returns the angle theta from the conversion of rectangular coordinates (x, y)
        // to polar coordinates (r, theta). This method computes the phase theta by
        // computing an arc tangent of y/x in the range of -pi to pi.
        // Per utilizzare atan2 con due punti si calcola la differenza tra le coordinate
        // x e le coordinate y dei due punti.
        deltaX01 = getPoint1().x - getPoint0().x;
        deltaY01 = getPoint1().y - getPoint0().y;

        val01 = Math.atan2(deltaY01, deltaX01) + getCorrezione();

        if (val01 <= 0) {
            val01 = 2 * Math.PI + val01;
        }

        return Math.toDegrees(val01);
    }

    public double valoreAngolo02() {

        double val02;
        int deltaX02;
        int deltaY02;

        deltaX02 = getPoint2().x - getPoint0().x;
        deltaY02 = getPoint2().y - getPoint0().y;

        val02 = Math.atan2(deltaY02, deltaX02) + correzione;
        if (val02 <= 0) {
            val02 = 2 * Math.PI + val02;
        }

        return Math.toDegrees(val02);
    }

    public double arcoTangente(Point v0, Point v) {

        double valAtan;
        int deltaX;
        int deltaY;
        deltaX = v.x - v0.x;
        deltaY = v0.y - v.y;

        valAtan = Math.atan2(deltaY, deltaX);

        return Math.toDegrees(valAtan);
    }

    public double valoreAngolo() {

        double val102;

        // angolo01 (NOA)
        double val01 = valoreAngolo01();
        // angolo02 (NOB)
        double val02 = valoreAngolo02();

        if (val01 > val02) {
            val102 = (Math.toDegrees(2 * Math.PI)) - (val01 - val02);
        } else {
            val102 = (val02 - val01);
        }

        // val102 is in degrees
        return val102;
    }
} 
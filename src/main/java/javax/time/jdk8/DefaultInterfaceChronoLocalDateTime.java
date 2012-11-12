/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.jdk8;

import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;

import java.util.Objects;

import javax.time.calendrical.DateTime;
import javax.time.calendrical.LocalDateTimeField;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.Chrono;
import javax.time.chrono.ChronoLocalDateTime;
import javax.time.format.CalendricalFormatter;

/**
 * A temporary class providing implementations that will become default interface
 * methods once integrated into JDK 8.
 *
 * @param <C> the chronology of this date-time
 */
public abstract class DefaultInterfaceChronoLocalDateTime<C extends Chrono<C>>
        extends DefaultInterfaceDateTime
        implements ChronoLocalDateTime<C> {

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDateTime<C> with(WithAdjuster adjuster) {
        return (ChronoLocalDateTime<C>) super.with(adjuster);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDateTime<C> plus(PlusAdjuster adjuster) {
        return (ChronoLocalDateTime<C>) super.plus(adjuster);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDateTime<C> minus(MinusAdjuster adjuster) {
        return (ChronoLocalDateTime<C>) super.minus(adjuster);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDateTime<C> minus(long amountToSubtract, PeriodUnit unit) {
        return (ChronoLocalDateTime<C>) super.minus(amountToSubtract, unit);
    }

    //-------------------------------------------------------------------------
    @Override
    public DateTime doWithAdjustment(DateTime dateTime) {
        return dateTime
                .with(EPOCH_DAY, getDate().getLong(LocalDateTimeField.EPOCH_DAY))
                .with(NANO_OF_DAY, getTime().toNanoOfDay());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(Query<R> query) {
        if (query == Query.ZONE_ID) {
            return null;
        } else if (query == Query.CHRONO) {
            return (R) getDate().getChrono();
        }
        return query.doQuery(this);
    }

    //-------------------------------------------------------------------------
    @Override
    public int compareTo(ChronoLocalDateTime<?> other) {
        int cmp = Long.compare(getLong(EPOCH_DAY), other.getLong(EPOCH_DAY));
        if (cmp == 0) {
            cmp = Long.compare(getLong(NANO_OF_DAY), other.getLong(NANO_OF_DAY));
            if (cmp == 0) {
                cmp = getDate().getChrono().compareTo(other.getDate().getChrono());
            }
        }
        return cmp;
    }

    @Override
    public boolean isAfter(ChronoLocalDateTime<?> other) {
        long thisEpDay = this.getLong(EPOCH_DAY);
        long otherEpDay = other.getLong(EPOCH_DAY);
        return thisEpDay > otherEpDay ||
            (thisEpDay == otherEpDay && this.getLong(NANO_OF_DAY) > other.getLong(NANO_OF_DAY));
    }

    @Override
    public boolean isBefore(ChronoLocalDateTime<?> other) {
        long thisEpDay = this.getLong(EPOCH_DAY);
        long otherEpDay = other.getLong(EPOCH_DAY);
        return thisEpDay < otherEpDay ||
            (thisEpDay == otherEpDay && this.getLong(NANO_OF_DAY) < other.getLong(NANO_OF_DAY));
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChronoLocalDateTime) {
            return compareTo((ChronoLocalDateTime<?>) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getDate().hashCode() ^ getTime().hashCode();
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        return getDate().toString() + 'T' + getTime().toString();
    }

    @Override
    public String toString(CalendricalFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.print(this);
    }

}
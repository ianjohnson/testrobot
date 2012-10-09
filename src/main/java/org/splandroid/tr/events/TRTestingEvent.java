/*
 * Copyright Ian Johnson 2012
 *
 * This file is part of TestRobot.
 *
 * TestRobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TestRobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TestRobot.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.splandroid.tr.events;

import java.util.Date;

class TRTestingEvent {
  final private TRTestEventKind kind;
  final private Date timeStamp;
  final private Throwable throwable;

  public TRTestingEvent(TRTestEventKind evtKind, Throwable thrable) {
    kind = evtKind;
    timeStamp = new Date();
    throwable = thrable;
  }

  public TRTestEventKind getKind() {
    return kind;
  }

  public Date getTimeStamp() {
    return timeStamp;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((kind == null) ? 0 : kind.hashCode());
    result = prime * result + ((throwable == null) ? 0 : throwable.hashCode());
    result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final TRTestingEvent other = (TRTestingEvent )obj;
    if (kind == null) {
      if (other.kind != null)
        return false;
    } else if (!kind.equals(other.kind))
      return false;
    if (throwable == null) {
      if (other.throwable != null)
        return false;
    } else if (!throwable.equals(other.throwable))
      return false;
    if (timeStamp == null) {
      if (other.timeStamp != null)
        return false;
    } else if (!timeStamp.equals(other.timeStamp))
      return false;
    return true;
  }
}

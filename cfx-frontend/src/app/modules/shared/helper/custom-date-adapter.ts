import { NativeDateAdapter } from "@angular/material/core";

export class CustomDateAdapter extends NativeDateAdapter {
  parse(value: any): Date | null {
    if ((typeof value === 'string') && value.length > 0) {
      const str = value.split(/[./-]/);
      const year = Number(str[2]);
      const month = Number(str[1]) - 1;
      const date = Number(str[0]);
      return new Date(year, month, date);
    }
    const timestamp = typeof value === 'number' ? value : Date.parse(value);
    return isNaN(timestamp) ? null : new Date(timestamp);
  }
  format(date: Date, displayFormat: { year?, month?, day?}): string {
    const { year, month, day } = displayFormat;
    if (year === 'numeric' && month === 'numeric' && day === 'numeric') {
      const days = ("0" + date.getDate()).slice(-2);
      const months = ("0" + (date.getMonth() + 1)).slice(-2);
      const year = date.getFullYear();
      return days + '/' + months + '/' + year;
    }
    return super.format(date, displayFormat);
  }

}
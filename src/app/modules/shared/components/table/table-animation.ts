import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';

export /** @type {*} */
const tableAnimation = [
  trigger('itemStatus', [
    state('deleted', style({ display: 'none' })),
    transition(
      '* => deleted',
      animate(
        '1.5s 10ms',
        keyframes([
          style({ opacity: '1', background: '#ff5050' }),
          style({ opacity: '0.5' }),
          style({ opacity: '0.4' }),
          style({ opacity: '0.3' }),
          style({ opacity: '0.1' }),
        ]),
      ),
    ),
  ]),
  trigger('detailExpand', [
    state('void', style({ height: '0px', minHeight: '0', visibility: 'hidden' })),
    state('*', style({ height: '*', visibility: 'visible' })),
    transition('void <=> *', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
  ]),
];

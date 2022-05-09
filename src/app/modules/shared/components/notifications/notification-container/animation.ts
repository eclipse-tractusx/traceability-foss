import { animate, state, style, transition, trigger } from '@angular/animations';

/**
 * Notification message animation
 * Slides the message and the status bar from right to left and the opposite way
 */
export /** @type {*} */
const notifyAnimation = trigger('notify', [
  state(
    'void',
    style({
      opacity: 0,
      height: 0,
      transform: 'translateX(100%)',
    }),
  ),
  state('show', style({ transform: 'translateX(5%)' })),
  transition('void => show, show => void', [animate('0.70s')]),
]);

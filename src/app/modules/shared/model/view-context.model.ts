import { View } from './view.model';

export class ViewContext<T> {
  // TODO: THIS IMPLICIT MIGHT NOT BE NECESSARY
  public $implicit: View<T>;
  public view: View<T>;
}

export interface ViewData<T> {
  data: T;
}

export interface ViewError {
  error: Error;
}

export interface ViewLoader {
  loader: boolean;
}

type OptionalViewData<T> = Partial<ViewData<T>>;
type OptionalViewError = Partial<ViewError>;
type OptionalViewLoader = Partial<ViewLoader>;

export class View<T> implements OptionalViewData<T>, OptionalViewError, OptionalViewLoader {
  data?: T;
  loader?: boolean;
  error?: Error;
}

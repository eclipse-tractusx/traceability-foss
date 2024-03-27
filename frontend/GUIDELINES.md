## Development Guidelines

We follow the recommended guidelines from the [angular framework](https://angular.io/guide/styleguide).

## Code Conventions

### Coding Style

Try your best to apply the single responsibility principle (SRP) to all components, services, and other symbols.

This helps make the app cleaner, easier to read, maintainable and more testable.

Classes should be named upper camel case followed by the specific suffix.
Depending on the usage of components, services and so on.

Properties and methods should be lower camel cases.

The properties must be at the top of your component, and the public properties should come first followed by the private ones. This rule is also applicable to methods.

Common component structure:

- Decorators (@Input, @Output, @ViewChild)
- Public properties
- Private properties
- Class constructor
- Lifecycle hooks
- Public methods
- Private methods

Be careful when importing external libraries. Only import what is needed. Avoid the `*` tag.

Do define one thing, such as a service or component, per file.

Consider limiting files to 400 lines of code.

### Small methods

Do define small methods

Consider limiting to no more than 75 lines.

### RXJS

Angular works with [rxjs](https://rxjs-dev.firebaseapp.com/guide/overview) behind the scenes, so you should have a good knowledge of this library.

RxJS is a library for composing asynchronous and event-based programs by using observable sequences.

> Think of RxJS as Lodash for events - this is the statement you find on the rxjs website.

A great use case is when you have to manage API requests since it could return an observable.

RXJS has a great arsenal of operators which might be handy.

Here you find the minimal rxjs operators to be aware of:

- `map`
- `merge`
- `concat`
- `mergeMap` & `switchMap`
- `combineLatest`
- `filter`
- `zip`
- `scan` & `reduce`
- `take` & `takeWhile`
- `tap`
- `debounceTime`
- `distinctUntilChanged`
- `delay`
- `from` & `fromEvent`

### Lodash

Whenever possible, we want to use a functional programming approach using pre-defined JavaScript methods instead of reinventing the wheel.

All developers should familiarize themselves with the methods offered by [Lodash](https://www.lodash.com).

Here is the minimal Lodash arsenal to be aware of:

- `intersection` & `intersectionWith`
- `union` & `unionWith`
- `uniq` & `uniqWith`
- `zipObject`
- `find`
- `groupBy`
- `partition`
- `some`
- `sortBy`
- `isEmpty`
- `isEqual`
- `get`
- `merge` & `mergeWith`
- `pickBy`

Learn about the difference between Vanilla JS `filter`/`map`/`reduce`/etc. and Lodash `filter`/`map`/`reduce`/etc.

For example, you can use Lodash's versions not just on arrays but also on objects. This makes the use of combinations of `.map` and `.filter` with `Object.entries`, `Object.values`, and `Object.keys` obsolete and makes code shorter and more readable.

> **Note:** Keep in mind, that if you need to use this type of utility method with observables, rxjs is there for you.

### Interface and Type Names

Do not use “I” at the beginning of an interface or type names. For example, do not write `IState` or `IView`.

### Observables & Subjects

By convention observables and subjects should end with a `$` sign.

### JavaScript Operators

The most common operators, [logical](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators) and otherwise, include:

- logical NOT `!` in front of words
- double NOT `!!` in front of words
- logical AND `&&` used for [short-circuit evaluation](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators#Short-circuit_evaluation)
- logical OR `||` used for short-circuit evaluation and default values
- ternary checks using `? :`
- [optional chaining](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Optional_chaining) using `?.`
- using `?` for optional arguments in functions
- [nullish coalescing](https://medium.com/@bramus/esnext-javascript-nullish-coalescing-operator-3e56afb64b54) `??` as a replacement for logical OR `||` when used as a default value
- [non-null assertion operator](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-2-0.html#non-null-assertion-operator) invoked by using `!` after a variable

<strong>IMPORTANT:</strong> Please use the non-null assertion operator very sparingly! Overriding a null/undefined type check is generally not a good idea.
When you write the code, it may seem safe but later refactoring can introduce real null or undefined values which will no longer be caught by the type checker.

### Typescript

Strive to improve TypeScript knowledge.

There are many features TypeScript offers to catch potential problems before running the compiler.
Developers should strive to improve their knowledge of TypeScript and use its features in our code base.

Things like:

- [Generics](https://www.typescriptlang.org/docs/handbook/2/generics.html)
- [Keyof Type Operator](https://www.typescriptlang.org/docs/handbook/2/keyof-types.html)
- [Utility Types](https://www.typescriptlang.org/docs/handbook/utility-types.html)
- [Never and Unknown Types](https://blog.logrocket.com/when-to-use-never-and-unknown-in-typescript-5e4d6c5799ad/)
- ...

> **Note:** Avoid the "any" type.

## Styling

### Naming HTML Classes

To ensure a homogeneous nomenclature of the HTML classes we adopted the BEM methodology;

Some old code might not be compliant with this practice, but try to follow this pattern in future developments.

Please read the [docs](http://getbem.com/naming/);

### CSS Framework

We use a mixture of [Angular Material](https://material.angular.io/) and [tailwind css](https://tailwindcss.com/docs) to style our app.
Tailwind is a utility-first CSS framework which comes with a lot of built-in CSS classes.

Tailwind is well documented, but you could also check this cheat sheet [here](https://github.com/LeCoupa/awesome-cheatsheets/blob/master/frontend/tailwind.css), if you prefer.

Tailwind is highly customizable so, if you intend to add any specific configurations, you should do it on the `tailwind config` file.

Any global CSS must be added to the layer base on the `base.scss` file.

Tailwind makes the process of styling easy by providing classes ready to be integrated into the HTML.

To avoid any unnecessary "noise" on the HTML pages, we suggest applying those styles to the SCSS files.

E.g:

```html
<div class="assets-grid"></div>
```

```scss
.assets-grid {
  @apply grid grid-cols-12 gap-2 w-full h-full sm:pl-4 md:pl-4 lg:pl-8 relative;
}
```

We are using [stylelint](https://stylelint.io/) for css validation.

```json
{
  "extends": "stylelint-config-recommended",
  "rules": {
    "at-rule-no-unknown": [
      true,
      {
        "ignoreAtRules": ["extends", "tailwind", "layer", "apply", "include", "mixin"]
      }
    ],
    "declaration-block-trailing-semicolon": null,
    "no-descending-specificity": null
  }
}
```

You might find some CSS warnings on vscode. To disable those, you must configure vscode settings.json with the following:

```json
{
  "css.validate": false,
  "less.validate": false,
  "scss.validate": false
}
```

This prevents the default linter from validating the css.

### Capitalization
Please make sure to follow this [capitalization guideline](https://developers.google.com/style/capitalization) to keep consistent capitalization throughout the UI.

The most significant rules are:
- The start of a header / label / sentence begins with an upper case followed by lowercase letters
- Domain specific names such as "As-Built", "As-Planned" can also be written in upper case even when the letter is not at the start of a header / label / sentence.

### Icons

We use the open source icons from [Material Icons](https://fonts.google.com/icons?query=Material+Icon).

With the help of [Icon | Angular material](https://material.angular.io/components/icon/overview) we display these icons in the application.

### EsLint

Please make sure you follow the linting rules by fixing all the errors and warnings which might arise.

You can check those alerts by running the command:

- `npm run lint`

This app uses the recommended rules for typescript.

### Code Formatter

We use [prettier](https://prettier.io/) style guide to format the files structure;

```json
{
  "semi": true,
  "trailingComma": "all",
  "singleQuote": true,
  "printWidth": 120,
  "tabWidth": 2,
  "arrowParens": "avoid"
}
```

To verify and adjust the format of each document, run the command:

- `npm run format`

### Git hooks

To ensure that all code is in sync with the rules mentioned above, we've implemented some git hooks using the husky library.

```json
  "husky": {
    "hooks": {
      "pre-commit": "npm run format && npm run lint",
      "pre-push": "npm run test:ci"
    }
  },
```

Make sure all linting warnings and errors are fixed and all the tests are asserted before you push the code.

We also provide some scripts to verify the tests before deploying the code:

- `npm run [script]`

```json
    "test": "ng test --code-coverage --watch=true --browsers=Chrome",
    "test:ci": "ng test --code-coverage --watch=false --browsers=ChromeHeadless"
```

## Bonus: VSCode extensions

Some VSCode extensions that might improve your coding experience :)

- Angular files
- Angular language service
- Angular schematics
- Auto rename tag
- Color info
- Color picker
- CSS peek
- Code spell checker
- Debugger for firefox
- Document this
- ESlint
- HTML CSS support
- Import cost
- Indent-rainbow
- JavaScript code snippets
- Material icon theme
- npm
- Open browser preview
- Path intellisense
- Prettier
- Stylelint
- Tailwind CSS intellisense
- TODO highlight
- TODO tree
- Version lens
- Visual Studio IntelliCode
- Yaml

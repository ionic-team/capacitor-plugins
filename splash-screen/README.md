# @capacitor/splash-screen

The Splash Screen API provides methods for showing or hiding a Splash image.

## Install

```bash
npm install @capacitor/splash-screen
npx cap sync
```

## API

<docgen-index>

* [`show(...)`](#show)
* [`hide(...)`](#hide)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### show(...)

```typescript
show(options?: ShowOptions | undefined) => Promise<void>
```

Show the splash screen

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#showoptions">ShowOptions</a></code> |

**Since:** 1.0.0

--------------------


### hide(...)

```typescript
hide(options?: HideOptions | undefined) => Promise<void>
```

Hide the splash screen

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#hideoptions">HideOptions</a></code> |

**Since:** 1.0.0

--------------------


### Interfaces


#### ShowOptions

| Prop                  | Type                 | Description                                                         | Default           | Since |
| --------------------- | -------------------- | ------------------------------------------------------------------- | ----------------- | ----- |
| **`autoHide`**        | <code>boolean</code> | Whether to auto hide the splash after showDuration                  |                   | 1.0.0 |
| **`fadeInDuration`**  | <code>number</code>  | How long (in ms) to fade in.                                        | <code>200</code>  | 1.0.0 |
| **`fadeOutDuration`** | <code>number</code>  | How long (in ms) to fade out.                                       | <code>200</code>  | 1.0.0 |
| **`showDuration`**    | <code>number</code>  | How long to show the splash screen when autoHide is enabled (in ms) | <code>3000</code> | 1.0.0 |


#### HideOptions

| Prop                  | Type                | Description                   | Default          | Since |
| --------------------- | ------------------- | ----------------------------- | ---------------- | ----- |
| **`fadeOutDuration`** | <code>number</code> | How long (in ms) to fade out. | <code>200</code> | 1.0.0 |

</docgen-api>

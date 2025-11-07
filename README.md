# 🔗 Multipart API

A simple and extensible API for creating multipart blocks in Minecraft mods.

Supports both **NeoForge** and **Fabric** platforms using a unified API.

---

## 📦 Installation

### Step 1: Add the repository

In your `build.gradle`, add this to your `repositories` block:

```kotlin
maven { url = "https://maven.liukrast.net/" }
````

### Step 2: Add the dependency

In your `dependencies`, include the library like this:

```kotlin
implementation("net.liukrast:multipart_api-${minecraft_version}:${multipart_version}")

//Optional, it is recommended to register the api for JarInJar
jarJar("net.liukrast:multipart_api-${minecraft_version}:[${multipart_version},)")
```

Supports **NeoForge** and **Fabric** automatically based on your environment.

---

## 🧠 Usage

The API is designed to be minimal and straightforward.

Basically we create a new block with a new blockstate called `parts`, which is an integer property (stores numbers).

You will define all positions of your multipart block, and the API will handle the rest:

if I create a multipart 6x2x3 (six blocks on the X, two on the Y and three on the Z),
it will automatically generate a blockstate `parts` with numbers from 0 -> 35.
The API automatically handles everything, block placement, block destroy, spacing problems (maybe theres not enough space to place that block)

### 🔨 Extend the base multipart block

Create a block that extends `AbstractMultipartBlock` (or `AbstractFacingMultipartBlock` if you need horizontal orientation):

```java
public class ExampleMultipartBlock extends AbstractMultipartBlock {
    public ExampleMultipartBlock(Properties properties) {
        super(properties);
    }
    
    //Adds two parts to the multiblock
    //Each part will have a specific id (int) based on the order in which they are added to this builder
    @Override
    public void defineParts(Builder builder) {
        builder.define(0, 0, 0);
        builder.define(0, 1, 0);
    }
}
```

That's basically it... stupid, isn't it? 
Well, you can play around with some other things, of course.

#### Simple example: placing your block from another position
If you create a big multipart with a simple loop, you will get something weird:
```java
    @Override
    public void defineParts(Builder builder) {
        for(int x = 0; x < 3; x++) {
            for(int y = 0; y < 2; y++) {
                builder.define(x, y, 0);
            }
        }
    }
```

But now... hey! My block always places from one corner instead of the center

There are two ways to solve this:
##### A. Recommended: use default state
Define in the constructor which part you want to get placed on the block you actually right-clicked:
```java
    public MyMultiPartWithMoreThanFourParts(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(getPartsProperty(), 4));
    }
```
And now it will place part 4 on the block you were aiming when right-clicked with the block in your hand!
##### B. Unrecommended: defining parts manually
You can also play around with parts to get the same result
```java
    @Override
    public void defineParts(Builder builder) {
        builder.define(0,0,0); //ORIGIN, the block that will be placed where i right-click
        builder.define(-1,0,0); //on the left of the origin
        builder.define(1,0,0); //on the right of the origin
    }
```

#### Simple example: different shapes for state
Let's say your multipart has a complex model: you can define a shape for each part like this:

```java
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int part = getPartsProperty();
        if(part == 0) {
            return YOUR_SHAPE_FOR_PART_0;
        }
        else return YOUR_SHAPE_FOR_OTHERS;
    }
```

Of course, you can play around with switches and other states!

#### Complex case: the IMultiPartBlock Interface
Starting from 1.0.4, a new interface has been added which will be useful in more restricted cases.

Let's say you already have a class to extend for your block... some other block class that you can't avoid extending...
In that case you wouldn't be able to extends `AbstractMultipartBlock`... so here is the solution.
Instead, you can implement `IMultiPartBlock`, but be careful, because you will have a few methods to implement which are already pre-implemented in the abstract class.


---

---

✨ Crafted with ❤️ by LiukRast

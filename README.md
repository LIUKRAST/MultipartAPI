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
```

Supports **NeoForge** and **Fabric** automatically based on your environment.

---

## 🧠 Usage

The API is designed to be minimal and straightforward.

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

---

---

✨ Crafted with ❤️ by LiukRast

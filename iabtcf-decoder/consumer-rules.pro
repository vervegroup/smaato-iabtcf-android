# ============================================
# Keep public API
# ============================================
-keep public class com.smaato.iabtcf.decoder.** { public *; }
-keep public interface com.smaato.iabtcf.** { *; }

# ============================================
# API 21-23 compatibility: Inner class rules
# ============================================

-keep class com.smaato.iabtcf.utils.FieldDefs { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$* { *; }
-keep class com.smaato.iabtcf.utils.BitReader { *; }
-keep class com.smaato.iabtcf.utils.BitReader$* { *; }
-keep class com.smaato.iabtcf.utils.BitReaderFunction { *; }
-keep class com.smaato.iabtcf.utils.LengthOffsetCache { *; }

# Keep the inner class ↔ enclosing class relationship metadata
-keepattributes InnerClasses, EnclosingMethod

# Keep annotations and signatures for reflection
-keepattributes *Annotation*, Signature, Exceptions
-keep class com.smaato.iabtcf.** { *; }
-keepnames class com.smaato.iabtcf.**

-dontshrink


-keep interface com.smaato.iabtcf.** { *; }
-keepnames interface com.smaato.iabtcf.**


-keep class com.smaato.iabtcf.utils.FieldDefs$* {
    *;
}

-keepnames class com.smaato.iabtcf.utils.FieldDefs$*


-keep class com.smaato.iabtcf.utils.FieldDefs$LengthSupplierFunctionAdapter {
    <init>(com.smaato.iabtcf.utils.FieldDefs$LengthSupplier);
    public java.lang.Integer apply(com.smaato.iabtcf.utils.BitReader);
}

-keep class com.smaato.iabtcf.utils.FieldDefs$OffsetSupplierFunctionAdapter {
    <init>(com.smaato.iabtcf.utils.FieldDefs$OffsetSupplier);
    public java.lang.Integer apply(com.smaato.iabtcf.utils.BitReader);
}

-keepnames class com.smaato.iabtcf.utils.FieldDefs$LengthSupplierFunctionAdapter
-keepnames class com.smaato.iabtcf.utils.FieldDefs$OffsetSupplierFunctionAdapter


-keep class com.smaato.iabtcf.utils.FieldDefs$PptcCustomPurposesConsentSupplier { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$PptcCustomPurposesLiTransparencySupplier { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$V1PpcCustomPurposesBitfieldSupplier { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$OffsetSupplierNotSupported { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$OffsetSupplierConstant { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$OffsetSupplierFrom { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$OffsetSupplierFromPrevious { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$LengthSupplierConstant { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$PublisherRestrictionLengthSupplier { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$BitRangeFieldLengthSupplier { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$BitRangeFieldV1LengthSupplier { *; }
-keep class com.smaato.iabtcf.utils.FieldDefs$MemoizingFunction { *; }


-keep interface com.smaato.iabtcf.utils.FieldDefs$LengthSupplier { *; }
-keep interface com.smaato.iabtcf.utils.FieldDefs$OffsetSupplier { *; }
-keep interface com.smaato.iabtcf.utils.BitReaderFunction { *; }


-keep class * implements com.smaato.iabtcf.utils.FieldDefs$LengthSupplier { *; }
-keep class * implements com.smaato.iabtcf.utils.FieldDefs$OffsetSupplier { *; }
-keep class * implements com.smaato.iabtcf.utils.BitReaderFunction { *; }
-keep class * implements java.util.function.Function { *; }


-keep class com.smaato.iabtcf.utils.BitReader { *; }
-keep class com.smaato.iabtcf.utils.BitReader$* { *; }


-keep class com.smaato.iabtcf.decoder.** { *; }
-keep class com.smaato.iabtcf.encoder.** { *; }
-keep class com.smaato.iabtcf.extras.** { *; }


# Keep InnerClasses attributes (CRITICAL for API 21)
-keepattributes InnerClasses
-keepattributes EnclosingClass
-keepattributes EnclosingMethod

# Keep all annotations
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes Signature

# Keep line numbers for better crash reports
-keepattributes SourceFile
-keepattributes LineNumberTable

# Keep local variable names for debugging
-keepattributes LocalVariableTable
-keepattributes LocalVariableTypeTable


-keepclasseswithmembernames class com.smaato.iabtcf.** {
    <init>(...);
    <clinit>(...);
    *** *(...);
}


# Keep Android classes
-keep class android.** { *; }
-keep class androidx.** { *; }

# Don't warn about Android classes
-dontwarn android.**
-dontwarn androidx.**

# ============================================
# OPTIMIZATION SETTINGS
# ============================================

# Safe optimization settings for API 21
-optimizationpasses 5
-allowaccessmodification

# Conservative optimizations
-optimizations !method/inlining/short,! code/simplification/arithmetic,! field/*,!class/merging/*

# ============================================
# VERBOSE OUTPUT
# ============================================

# Print what's being kept/removed
-verbose
-printmapping proguard-mapping.txt
-printconfiguration proguard-config.txt

# Never obfuscate class names
-dontobfuscate

# Never optimize away code
-dontoptimize

# Keep everything in IABTCF package
-keeppackagenames com.smaato.iabtcf.**
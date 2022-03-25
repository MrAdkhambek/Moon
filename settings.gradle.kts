enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "MoonLib"


include(
    "example",
    "moon",

    "convertors:KotlinxSerialization",
    "convertors:Gson"
)

#if CODE_ANALYSIS
using System.Diagnostics.CodeAnalysis;

[module: SuppressMessage("Microsoft.Usage", "CA1806:DoNotIgnoreMethodResults", Scope = "member", Target = "WallpaperSetter.Program.#Main()", MessageId = "WallpaperSetter.Form1")]
[module: SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes", Scope = "member", Target = "WallpaperSetter.Form1.#LogEvent(System.String)")]

#endif

#if CODE_ANALYSIS
using System.Diagnostics.CodeAnalysis;

[module: SuppressMessage("Microsoft.Usage", "CA1806:DoNotIgnoreMethodResults", Scope = "member", Target = "WallpaperSetter.Program.#Main()", MessageId = "WallpaperSetter.Form1")]
[module: SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes", Scope = "member", Target = "WallpaperSetter.Form1.#LogEvent(System.String)")]

// ignore about WindowsAPI class
[module: SuppressMessage("Microsoft.Performance", "CA1812:AvoidUninstantiatedInternalClasses", Scope = "type", Target = "Cycler.WindowsAPI")]
[module: SuppressMessage("Microsoft.Globalization", "CA2101:SpecifyMarshalingForPInvokeStringArguments", Scope = "member", Target = "Cycler.WindowsAPI.#SystemParametersInfo(System.Int32,System.Int32,System.String,System.Int32)", MessageId = "2")]
[module: SuppressMessage("Microsoft.Design", "CA1060:MovePInvokesToNativeMethodsClass", Scope = "member", Target = "Cycler.WindowsAPI.#SystemParametersInfo(System.Int32,System.Int32,System.String,System.Int32)")]
[module: SuppressMessage("Microsoft.Usage", "CA1806:DoNotIgnoreMethodResults", Scope = "member", Target = "Cycler.WindowsAPI.#SetDesktopBackground(System.Drawing.Bitmap,Cycler.DesktopBackgroundStyle)", MessageId = "Cycler.WindowsAPI.SystemParametersInfo(System.Int32,System.Int32,System.String,System.Int32)")]

#endif

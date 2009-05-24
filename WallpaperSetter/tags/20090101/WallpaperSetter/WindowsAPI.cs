using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.IO;
using Microsoft.Win32;
using System.Runtime.InteropServices;

namespace Cycler
{
    class WindowsAPI
    {
        /// <summary>
        /// Adds the named item and its path to the Current User
        /// startup in registry.
        /// </summary>
        /// <param name="name"></param>
        /// <param name="path"></param>
        public static void AddStartupItem(string name, string path)
        {
            RegistryKey key = Registry.CurrentUser.OpenSubKey(
                @"Software\Microsoft\Windows\CurrentVersion\Run", true);

            key.SetValue(name, path);
        }

        /// <summary>
        /// Removes the named item from Current User startup in registry.
        /// </summary>
        /// <param name="name"></param>
        public static void RemoveStartupItem(string name)
        {
            RegistryKey key = Registry.CurrentUser.OpenSubKey(
                @"Software\Microsoft\Windows\CurrentVersion\Run", true);

            key.DeleteValue(name, false);
        }

        /// <summary>
        /// Set the Desktop background to the supplied image.  Image must be
        /// saved as BMP (saves to My Pictures), and uses the specified
        /// style (Tiled, Centered, Stretched).
        /// </summary>
        /// <param name="img"></param>
        /// <param name="style"></param>
        public static void SetDesktopBackground(Bitmap img, DesktopBackgroundStyle style)
        {
            string picturesPath = Path.Combine( 
                System.Environment.GetFolderPath(Environment.SpecialFolder.MyPictures), 
                "coding4fun-desktop.bmp");
            
            // Clear the current background (releases lock on current bitmap)
            SystemParametersInfo(SPI_SETDESKWALLPAPER, 0,
                "",
                SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE);

            // Convert to BMP and save)
            img.Save(picturesPath,
                  System.Drawing.Imaging.ImageFormat.Bmp);

            //Write style info to registry
            RegistryKey key = Registry.CurrentUser.OpenSubKey("Control Panel\\Desktop", true);

            switch (style)
            {
                case DesktopBackgroundStyle.Stretched:
                    key.SetValue(@"WallpaperStyle", "2");
                    key.SetValue(@"TileWallpaper", "0");
                    break;

                case DesktopBackgroundStyle.Centered:
                    key.SetValue(@"WallpaperStyle", "1");
                    key.SetValue(@"TileWallpaper", "0");
                    break;

                case DesktopBackgroundStyle.Tiled:
                    key.SetValue(@"WallpaperStyle", "1");
                    key.SetValue(@"TileWallpaper", "1");
                    break;
            }

            SystemParametersInfo(SPI_SETDESKWALLPAPER, 0, 
                picturesPath, 
                SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE);
        }

        const int SPI_SETDESKWALLPAPER = 20;
        const int SPIF_UPDATEINIFILE = 0x01;
        const int SPIF_SENDWININICHANGE = 0x02;

        [DllImport("user32.dll", CharSet = CharSet.Auto)]
        static extern int SystemParametersInfo(
            int uAction, int uParam, string lpvParam, int fuWinIni);
    }

    /// <summary>
    /// Enumeration for desktop image background display styles.
    /// </summary>
    public enum DesktopBackgroundStyle : int
    {
        Tiled, Centered, Stretched
    }

}

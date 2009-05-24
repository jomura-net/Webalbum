using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Drawing;
using System.Net;
using System.IO;
using System.Web;
using System.Diagnostics;
using System.Configuration;

namespace WallpaperSetter
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            //InitializeComponent();
            string filePath;
            Bitmap bmp = GetImage(out filePath);
            LogEvent(filePath);
            Cycler.WindowsAPI.SetDesktopBackground(bmp,
                Cycler.DesktopBackgroundStyle.Centered);
        }

        static Bitmap GetImage(out string filePath)
        {
            string image_url = "http://jomura.net/picture/random";
            string adult = ConfigurationSettings.AppSettings["adult"];
            if (!string.IsNullOrEmpty(adult) && Boolean.Parse(adult))
            {
                image_url += "?adult";
            }
            using (WebClient wc = new WebClient())
            {
                wc.Headers.Add("Referer", "http://jomura.net/picture/");
                using (Stream imageStrm = wc.OpenRead(image_url))
                {
                    filePath = Uri.UnescapeDataString(
                        wc.ResponseHeaders["PictureFilePath"]);
                    return new Bitmap(imageStrm);
                }
            }
        }

        static void LogEvent(string filePath)
        {
            string sSource = System.Reflection.Assembly
                .GetExecutingAssembly().FullName;
            string sLog = "Application";
            string sEvent = "Changed wallpaper into \"" + filePath + "\"";

            if (!EventLog.SourceExists(sSource))
            {
                EventLog.CreateEventSource(sSource, sLog);
            }

            EventLog.WriteEntry(sSource, sEvent);
        }
    }
}

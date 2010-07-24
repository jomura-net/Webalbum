using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Drawing;
using System.Net;
using System.IO;
using System.Web;
using System.Diagnostics;
using System.Configuration;
using System.Drawing.Drawing2D;

namespace WallpaperSetter
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            try
            {
                //InitializeComponent();
                string filePath;
                Bitmap bmp = GetImage(out filePath);
                LogEvent(filePath);
                Cycler.WindowsAPI.SetDesktopBackground(bmp,
                    Cycler.DesktopBackgroundStyle.Centered);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, "Error Occured",
                    MessageBoxButtons.OK, 
                    MessageBoxIcon.Error, 
                    MessageBoxDefaultButton.Button1,
                    MessageBoxOptions.DefaultDesktopOnly);
            }
        }

        Bitmap GetImage(out string filePath)
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
                Bitmap bitmap = null;
                using (Stream imageStrm = wc.OpenRead(image_url))
                {
                    filePath = Uri.UnescapeDataString(
                        wc.ResponseHeaders["PictureFilePath"]);
                    bitmap = new Bitmap(imageStrm);
                }
                return changeSize(bitmap);
            }
        }

        Bitmap changeSize(Bitmap src)
        {
            string fit = ConfigurationSettings.AppSettings["FitScreen"];
            if (string.IsNullOrEmpty(fit) || !Boolean.Parse(fit))
            {
                return src;
            }

            double srcAspect = (double)src.Width / src.Height;
            double screenAspect = (double)Screen.PrimaryScreen.Bounds.Width / Screen.PrimaryScreen.Bounds.Height;

            int destWidth = Screen.PrimaryScreen.Bounds.Width;
            int destHight = Screen.PrimaryScreen.Bounds.Height;
            if (srcAspect > screenAspect)
            {
                destHight = (int)(Screen.PrimaryScreen.Bounds.Width / srcAspect);
            }
            else
            {
                destWidth = (int)(Screen.PrimaryScreen.Bounds.Height * srcAspect);
            }

            Bitmap dest = new Bitmap(destWidth, destHight);
            Graphics g = Graphics.FromImage(dest);
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;
            g.DrawImage(src, 0, 0, destWidth, destHight);
            return dest;
        }

        void LogEvent(string filePath)
        {
            string sSource = System.Reflection.Assembly
                .GetExecutingAssembly().GetName().Name;
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

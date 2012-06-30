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
using System.Security;

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
            catch (SecurityException se)
            {
                //イベントソースを作成できなかった場合
                MessageBox.Show("初回のみ、管理者権限で起動してください。", "WallpaperSetter");
                throw se;
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, "WallpaperSetter",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Error,
                    MessageBoxDefaultButton.Button1,
                    MessageBoxOptions.DefaultDesktopOnly);
            }
        }

        Bitmap GetImage(out string filePath)
        {
            string image_url = ConfigurationSettings.AppSettings["ImageURL"];
            string adult = ConfigurationSettings.AppSettings["adult"];
            if (!string.IsNullOrEmpty(adult) && Boolean.Parse(adult))
            {
                image_url += "?adult";
            }
            using (WebClient wc = new WebClient())
            {
                wc.Headers.Add("Referer", image_url);
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

            if (IsAdministrator() && !EventLog.SourceExists(sSource))
            {
                EventLog.CreateEventSource(sSource, sLog);
            }
            EventLog.WriteEntry(sSource, sEvent);
        }

        /// <summary>
        /// 現在アプリケーションを実行しているユーザーに管理者権限があるか調べる
        /// </summary>
        /// <returns>管理者権限がある場合はtrue。</returns>
        static bool IsAdministrator()
        {
            //現在のユーザーを表すWindowsIdentityオブジェクトを取得する
            System.Security.Principal.WindowsIdentity wi =
                System.Security.Principal.WindowsIdentity.GetCurrent();
            //WindowsPrincipalオブジェクトを作成する
            System.Security.Principal.WindowsPrincipal wp =
                new System.Security.Principal.WindowsPrincipal(wi);
            //Administratorsグループに属しているか調べる
            return wp.IsInRole(
                System.Security.Principal.WindowsBuiltInRole.Administrator);
        }
    }
}

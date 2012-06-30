using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Diagnostics;
using System.Security;

namespace WallpaperSetter
{
    static class Program
    {
        /// <summary>
        /// アプリケーションのメイン エントリ ポイントです。
        /// </summary>
        [STAThread]
        static void Main()
        {
            try
            {
                // 二重起動をチェックする
                if (System.Diagnostics.Process.GetProcessesByName(
                        System.Diagnostics.Process.GetCurrentProcess().ProcessName).Length > 1)
                {
                    // すでに起動していると判断して終了
                    MessageBox.Show("すでに起動中です。", "WallpaperSetter", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                    return;
                }

                /*
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new Form1());
                */
                new Form1();
            }
            catch (SecurityException)
            {
                // 管理者権限を要求する。
                RestartApplication();
            }
        }

        static void RestartApplication()
        {
            ProcessStartInfo psi = new ProcessStartInfo();
            psi.UseShellExecute = true;
            psi.WorkingDirectory = Environment.CurrentDirectory;
            psi.FileName = Application.ExecutablePath;
            psi.Verb = "runas";

            try
            {
                Process p = Process.Start(psi);
            }
            catch
            {
                // do nothing
            }
        }

    }
}
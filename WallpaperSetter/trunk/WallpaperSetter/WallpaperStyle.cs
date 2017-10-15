namespace WallpaperSetter
{
    /// <summary>壁紙の表示スタイル</summary>
    /// http://smdn.jp/programming/tips/setdeskwallpaper/
    class WallpaperStyle
    {
        /// <summary>中央に表示</summary>
        public static readonly WallpaperStyle Center = new WallpaperStyle(0, 0);

        /// <summary>並べて表示</summary>
        public static readonly WallpaperStyle Tile = new WallpaperStyle(0, 1);

        /// <summary>拡大して表示 (画面に合わせて伸縮)</summary>
        public static readonly WallpaperStyle Stretch = new WallpaperStyle(2, 0);

        /// <summary>リサイズして表示 (ページ縦幅に合わせる)</summary><remarks>Windows 7以降のみ</remarks>
        public static readonly WallpaperStyle ResizeFit = new WallpaperStyle(6, 0);

        /// <summary>リサイズして全体に表示 (ページ横幅に合わせる)</summary><remarks>Windows 7以降のみ</remarks>
        public static readonly WallpaperStyle ResizeFill = new WallpaperStyle(10, 0);

        internal readonly int _WallpaperStyle;
        internal readonly int _TileWallpaper;

        private WallpaperStyle(int wallpaperStyle, int tileWallpaper)
        {
            _WallpaperStyle = wallpaperStyle;
            _TileWallpaper = tileWallpaper;
        }
    }
}

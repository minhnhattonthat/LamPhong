package com.lamphongstore.lamphong.material;

import com.lamphongstore.lamphong.R;

/**
 * Created by Norvia on 03/04/2017.
 */

public class SideMenu {

    private static final SideMenuItem myPoint = new SideMenuItem(R.drawable.icon_promotion, "Điểm của tôi");
    private static final SideMenuItem myAccount = new SideMenuItem(R.drawable.icon_account, "Quản lý tài khoản");
    private static final SideMenuItem myRefCode = new SideMenuItem(R.drawable.ic_share, "Chia sẻ mã giới thiệu");
    private static final SideMenuItem myLamPhongChain = new SideMenuItem(R.drawable.icon_store, "Hệ thống LamPhongStore");
    private static final SideMenuItem myPointProgram = new SideMenuItem(R.drawable.icon_point, "Chương trình tích điểm");
    private static final SideMenuItem myPromotion = new SideMenuItem(R.drawable.icon_gift, "Chương trình khuyến mãi");
    private static final SideMenuItem myLogOut = new SideMenuItem(R.drawable.ic_logout, "Đăng xuất");
    public static final SideMenuItem[] SIDE_MENU_ITEM_LIST = {myPoint, myAccount, myRefCode, myLamPhongChain,
            myPointProgram, myPromotion, myLogOut};

    public static class SideMenuItem {
        private int iconMenu;
        private String titleMenu;

        SideMenuItem(int iconMenu, String titleMenu) {
            this.iconMenu = iconMenu;
            this.titleMenu = titleMenu;
        }

        public int getIconMenu() {
            return iconMenu;
        }

        public void setIconMenu(int iconMenu) {
            this.iconMenu = iconMenu;
        }

        public String getTitleMenu() {
            return titleMenu;
        }

        public void setTitleMenu(String titleMenu) {
            this.titleMenu = titleMenu;
        }

    }
}

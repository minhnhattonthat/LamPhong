package com.lamphongstore.lamphong.data;

import com.lamphongstore.lamphong.model.LPError;


/**
 * Created by HungNguyen on 4/4/17.
 */

public interface CallBackDelNoti {
    void onSuccess();

    void onError(LPError error);
}

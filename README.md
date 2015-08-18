# DrawerOnCorner
一个简单的抽屉控件 a simple drawer widget

点击左下角的按钮，抽屉内容便滑动而出，再次点击缩回。

具体的使用方法：

 ![image](https://github.com/gpfduoduo/DrawerOnCorner/tree/master/Drawer/screen capture/GIF.gif)
 
 
   <com.example.lenovo.drawerlibrary.Drawer
        android:id="@+id/drawer2"
        android:layout_width="wrap_content"
        android:layout_height="65dp"
        android:layout_gravity="left|bottom"
        drawer:drawer_closedBackground="@drawable/guide_drawer_handler"
        drawer:drawer_content="@+id/drawerContent"
        drawer:drawer_handle="@+id/drawerHandle"
        drawer:drawer_closeHandle="@+id/guide_drawer_close"
        drawer:drawer_handle_closeWidth="30dp"
        drawer:drawer_handle_openWidth="65dp"
        drawer:drawer_openedBackground="@drawable/guide_drawer_handler"
        drawer:drawer_position="left" >

        <ImageButton
            android:id="@+id/drawerHandle"
            android:contentDescription="@string/app_name"
            android:background="@drawable/guide_drawer_handler"
            android:src="@drawable/guide_drawer_handler_arrow_open"
            android:layout_width="65dp"
            android:layout_height="fill_parent" />

        <LinearLayout
            android:id="@+id/drawerContent"
            android:layout_width="wrap_content"
            android:layout_height="fill_parent"
            android:background="@drawable/guide_drawer_content"
            android:gravity="center_vertical" >

            <RelativeLayout
                android:id="@+id/guide_drawer_close"
                android:layout_width="30dp"
                android:layout_height="fill_parent"
                android:layout_marginLeft="10dp"
                android:onClick="doBtnAction" >

                <ImageView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_centerInParent="true"
                    android:src="@drawable/guide_drawer_handler_arrow_close" >
                </ImageView>
            </RelativeLayout>

            <RelativeLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginLeft="20dp" >

                <ImageView
                    android:id="@+id/guide_call2"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_centerHorizontal="true"
                    android:contentDescription="@string/app_name"
                    android:src="@drawable/guide_drawer_btn_call" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/guide_call2"
                    android:layout_centerHorizontal="true"
                    android:text="呼叫" />
            </RelativeLayout>

            <RelativeLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginLeft="20dp" >

                <ImageView
                    android:id="@+id/guide_snapshot2"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_centerHorizontal="true"
                    android:contentDescription="@string/app_name"
                    android:src="@drawable/guide_drawer_btn_snapshot" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/guide_snapshot2"
                    android:layout_centerHorizontal="true"
                    android:text="快拍" />
            </RelativeLayout>

            <RelativeLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginLeft="20dp" >

                <ImageView
                    android:id="@+id/guide_find2"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_centerHorizontal="true"
                    android:contentDescription="@string/app_name"
                    android:src="@drawable/guide_drawer_btn_find" />

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/guide_find2"
                    android:layout_centerHorizontal="true"
                    android:text="找人" />
            </RelativeLayout>
        </LinearLayout>
    </com.example.lenovo.drawerlibrary.Drawer>

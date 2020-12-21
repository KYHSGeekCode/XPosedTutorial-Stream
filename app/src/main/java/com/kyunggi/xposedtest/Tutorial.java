package com.kyunggi.xposedtest;

import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import com.android.bluetooth.opp.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;
import java.io.*;
import java.lang.reflect.*;
import javax.obex.*;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Tutorial implements IXposedHookLoadPackage
{
	private String TAG="Xposed Tutorial BTFix";
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
	{
		if (!lpparam.packageName.equals("com.android.bluetooth"))
		{
			Log.i(TAG, "Not: " + lpparam.packageName);
			return;
		}
		Log.i(TAG, "Yes " + lpparam.packageName);	

		findAndHookMethod("com.android.bluetooth.opp.BluetoothOppManager", lpparam.classLoader, "isWhitelisted", String.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.v(TAG, "HOOK DONE");
					param.setResult((Object)true);
					return;
				}

			});
		findAndHookMethod("com.android.bluetooth.opp.BluetoothOppObexClientSession.ClientThread", lpparam.classLoader, "sendFile", "com.android.bluetooth.opp.BluetoothOppSendFileInfo", new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					Log.v(TAG, "HOOK DONE");				
					param.setResult((Object)sendFile(param.thisObject,param.args[0],lpparam.classLoader));
					return;
				}
			});
		return;
	}
	private int sendFile(Object thisObject, /*BluetoothOppSendFileInfo*/Object fileInfoTmp,ClassLoader loader)
	{
		/*BluetoothOppObexClientSession*/Object outerThis=/* (BluetoothOppObexClientSession)*/ XposedHelpers.getSurroundingThis(thisObject);
		Object fileInfomFileName = XposedHelpers.getObjectField(fileInfoTmp,"mFileName");
		Object fileInfomMimetype = XposedHelpers.getObjectField(fileInfoTmp,"mMimetype");
		Object mInfo= XposedHelpers.getObjectField(thisObject, "mInfo");
		int mInfomId = XposedHelpers.getIntField(mInfo,"mId");
		String mInfomDestination = (String) XposedHelpers.getObjectField(mInfo,"mDestination");
		
		Uri mInfomUri = (Uri) XposedHelpers.getObjectField(mInfo,"mUri");
		String uripath=mInfomUri.getPath();
		int index=uripath.lastIndexOf("@");
		uripath=uripath.substring(0,index);
		File file=new File(uripath);	
		Log.d(TAG,"uripath="+uripath+"="+mInfomUri.toString());
		InputStream fileInfomInputStream=(InputStream) XposedHelpers.getObjectField(fileInfoTmp,"mInputStream");
		long fileInfomLength =file.length();//new File(mInfomUri.getPath()).length();// XposedHelpers.getLongField(fileInfoTmp,"mLength");
		//	Object mCs;
	//	BluetoothOppSendFileInfo fileInfo;
		{
		/*	String mFileName;
			String type;
			long length;
			FileInputStream fis;
			int status;
			mFileName=(String) XposedHelpers.getObjectField(fileInfoTmp,"mFileName");
		*/	
		}
	//	fileInfo=new BluetoothOppSendFileInfo();
	//	try
		//{
			//Class mInfoClass=Class.forName("com.android.bluetooth.opp.BluetoothOppSendFileInfo", false, loader);
			/*Field mInfoMidField=mInfoClass.getField("mId");
			XposedHelpers.getIntField(*/
		 	//XposedHelpers.getIntField(fileInfoTmp,"mId");
		//}
		
		boolean V=true;
	//	final int sSleepTime = 500;
		Context mContext1=(Context) XposedHelpers.getObjectField(thisObject, "mContext1");
		try
		{
			InputStream is=mContext1.getContentResolver().openInputStream(mInfomUri);
			try
			{
				/*fileInfomLength=*/is.available();
			}
			catch (IOException e)
			{
				Log.e(TAG,"",e);
			}
		}
		catch (FileNotFoundException e)
		{
			Log.e(TAG,"",e);
		}
		/*try
		{
			Class mInfoClass=Class.forName("com.android.bluetooth.opp.BluetoothOppSendFileInfo", false, loader);
			Field mInfoMidField=mInfoClass.getField("mId");
			//XposedHelpers.getIntField(
		}
		catch (ClassNotFoundException|NoSuchFieldException e)
		{
			Log.e(TAG,"minfo class failed!",e);
		}*/
		/*
		 public int mId;

		 public Uri mUri;

		 public String mHint;

		 public String mFilename;

		 public String mMimetype;

		 public int mDirection;

		 public String mDestination;

		 public int mVisibility;

		 public int mConfirm;

		 public int mStatus;

		 public long mTotalBytes;

		 public long mCurrentBytes;

		 public long mTimestamp;

		 public boolean mMediaScanned;
		 */
		//Field mInfoMid=
		
	//	boolean waitingForShare=XposedHelpers.getBooleanField(thisObject, "waitingForShare");
		//ObexTransport mTransport1=(ObexTransport) XposedHelpers.getObjectField(thisObject, "mTransport1");
		//ClientSession mCs;
		Object mCs=/*(ClientSession)*/ XposedHelpers.getObjectField(thisObject, "mCs");
		// WakeLock wakeLock;
		//BluetoothOppSendFileInfo mFileInfo = (BluetoothOppSendFileInfo)XposedHelpers.getObjectField(thisObject, "mFileInfo");
		//boolean mConnected = false;
		//int mNumShares=XposedHelpers.getIntField(thisObject, "mNumShares");

		boolean error = false;
		int responseCode = -1;
		long position = 0;
		int status = BluetoothShare.STATUS_SUCCESS;
		Uri contentUri = Uri.parse(BluetoothShare.CONTENT_URI + "/" + mInfomId);
		ContentValues updateValues;
		/*HeaderSet*/Object request= XposedHelpers.newInstance(XposedHelpers.findClass("javax.obex.HeaderSet",loader));// new HeaderSet();
		
		XposedHelpers.callMethod(request,"setHeader",(Object)HeaderSet.NAME, fileInfomFileName);
		
		XposedHelpers.callMethod(request,"setHeader",(Object)HeaderSet.TYPE, fileInfomMimetype);
		
		XposedHelpers.callStaticMethod(outerThis.getClass(),"applyRemoteDeviceQuirks", request, mInfomDestination, fileInfomFileName);

		Constants.updateShareStatus(mContext1,mInfomId,BluetoothShare.STATUS_RUNNING,loader);
		//XposedHelpers.callStaticMethod(XposedHelpers.findClass("Constants",loader),/*Constants.*/"updateShareStatus",mContext1, (Object)mInfomId, (Object)BluetoothShare.STATUS_RUNNING);
		
		XposedHelpers.callMethod(request,"setHeader",(Object)HeaderSet.LENGTH, (new Long(fileInfomLength)));
		/*ClientOperation*/Object putOperation = (ClientOperation) null;
		OutputStream outputStream = (OutputStream) null;
		InputStream inputStream = (InputStream) null;
		try
		{
			synchronized (thisObject)
			{
				XposedHelpers.setBooleanField(outerThis,"mWaitingForRemote", true);
			}
		//	try
		//	{
				if (V) Log.v(TAG, "put headerset for " + fileInfomFileName);
				putOperation =/*(ClientOperation)*/ XposedHelpers.callMethod(mCs,/* (ClientOperation)mCs.*/"put",request);
		/*	}
			catch (IOException e)
			{
				status = BluetoothShare.STATUS_OBEX_DATA_ERROR;
				Constants.updateShareStatus(mContext1, mInfomId, status);
				Log.e(TAG, "Error when put HeaderSet ");
				error = true;
			}*/
			synchronized (thisObject)
			{
				XposedHelpers.setBooleanField(outerThis,"mWaitingForRemote",false);
			}
			if (!error)
			{
				//try
				//{
					if (V) Log.v(TAG, "openOutputStream " + fileInfomFileName);
					outputStream = (OutputStream) XposedHelpers.callMethod( putOperation,"openOutputStream");
					inputStream = (InputStream) XposedHelpers.callMethod( putOperation,"openInputStream");
				//}
				/*catch (IOException e)
				{
					status = BluetoothShare.STATUS_OBEX_DATA_ERROR;
					Constants.updateShareStatus(mContext1, mInfomId, status);
					Log.e(TAG, "Error when openOutputStream");
					error = true;
				}*/
			}
			if (!error)
			{
				updateValues = new ContentValues();
				updateValues.put(BluetoothShare.CURRENT_BYTES, 0);
				updateValues.put(BluetoothShare.STATUS, BluetoothShare.STATUS_RUNNING);
				mContext1.getContentResolver().update(contentUri, updateValues, null, null);
			}
			if (!error)
			{
				int readLength = 0;
				long percent = 0;
				long prevPercent = 0;
				boolean okToProceed = false;
				long timestamp = 0;
				int outputBufferSize =4096;// XposedHelpers.callMethod( putOperation,"getMaxPacketSize");
				byte[] buffer = new byte[outputBufferSize];
				BufferedInputStream a = new BufferedInputStream(fileInfomInputStream, 0x4000);
				if (!XposedHelpers.getBooleanField(outerThis,"mInterrupted") && (position != fileInfomLength))
				{
					Handler mCallback=(Handler) XposedHelpers.getObjectField(outerThis,"mCallback");
					readLength = XposedHelpers.callStaticMethod(outerThis.getClass(), "readFully",(Object)a,(Object)buffer, (Object)outputBufferSize);
					mCallback.sendMessageDelayed(mCallback
												 .obtainMessage(BluetoothOppObexSession.MSG_CONNECT_TIMEOUT),
												 BluetoothOppObexSession.SESSION_TIMEOUT);
					synchronized (thisObject)
					{
						XposedHelpers.setBooleanField(outerThis,"mWaitingForRemote",true);
					}
					// first packet will block here
					outputStream.write(buffer, 0, readLength);
					position += readLength;
					/* check remote accept or reject */
					responseCode = XposedHelpers.callMethod( putOperation,"getResponseCode");
					mCallback.removeMessages(BluetoothOppObexSession.MSG_CONNECT_TIMEOUT);
					synchronized (thisObject)
					{
						XposedHelpers.setBooleanField(outerThis,"mWaitingForRemote",false);
					}
					if (responseCode == ResponseCodes.OBEX_HTTP_CONTINUE
						|| responseCode == ResponseCodes.OBEX_HTTP_OK)
					{
						if (V) Log.v(TAG, "Remote accept");
						okToProceed = true;
						updateValues = new ContentValues();
						updateValues.put(BluetoothShare.CURRENT_BYTES, position);
						mContext1.getContentResolver().update(contentUri, updateValues, null,null);
					}
					else
					{
						okToProceed=true;
						Log.i(TAG, "Remote reject, Response code is " + responseCode);
					}
				}
				while (!XposedHelpers.getBooleanField(outerThis,"mInterrupted") && okToProceed && (position < fileInfomLength))
				{
					if (V) timestamp = System.currentTimeMillis();
					readLength = a.read(buffer, 0, outputBufferSize);
					outputStream.write(buffer, 0, readLength);
					/* check remote abort */
					responseCode = XposedHelpers.callMethod( putOperation,"getResponseCode");
					if (V) Log.v(TAG, "Response code is " + responseCode);
					if (responseCode != ResponseCodes.OBEX_HTTP_CONTINUE
						&& responseCode != ResponseCodes.OBEX_HTTP_OK)
					{
						/* abort happens */
					/////***/**6*//*/////////*/////////	
						okToProceed = true;
					}
					else
					{
						position += readLength;
						if (V)
						{
							Log.v(TAG, "Sending file position = " + position
								  + " readLength " + readLength + " bytes took "
								  + (System.currentTimeMillis() - timestamp) + " ms");
						}
						// Update the Progress Bar only if there is change in percentage
						percent = position * 100 / fileInfomLength;
						if (percent > prevPercent)
						{
							updateValues = new ContentValues();
							updateValues.put(BluetoothShare.CURRENT_BYTES, position);
							mContext1.getContentResolver().update(contentUri, updateValues,
																  null, null);
							prevPercent = percent;
						}
					}
				}
				if (responseCode == ResponseCodes.OBEX_HTTP_FORBIDDEN
					|| responseCode == ResponseCodes.OBEX_HTTP_NOT_ACCEPTABLE)
				{
					Log.i(TAG, "Remote reject file " + fileInfomFileName + " length "
						  + fileInfomLength);
					status = BluetoothShare.STATUS_FORBIDDEN;
				}
				else if (responseCode == ResponseCodes.OBEX_HTTP_UNSUPPORTED_TYPE)
				{
					Log.i(TAG, "Remote reject file type " + fileInfomMimetype);
					status = BluetoothShare.STATUS_NOT_ACCEPTABLE;
				}
				else if (!XposedHelpers.getBooleanField(outerThis,"mInterrupted") && position == fileInfomLength)
				{
					Log.i(TAG, "SendFile finished send out file " + fileInfomFileName
						  + " length " + fileInfomLength);
				}
				else
				{
					error = true;
					status = BluetoothShare.STATUS_CANCELED;
				//	XposedHelpers.callMethod(	putOperation,"abort");
					/* interrupted */
					Log.i(TAG, "SendFile interrupted when send out file " + fileInfomFileName
						  + " at " + position + " of " + fileInfomLength);
				}
			}
		}
		catch (IOException e)
		{
			XposedHelpers.callMethod(thisObject,"handleSendException",e.toString());
		}
		catch (NullPointerException e)
		{
			XposedHelpers.callMethod(thisObject,"handleSendException",e.toString());
		}
		catch (IndexOutOfBoundsException e)
		{
			XposedHelpers.callMethod(thisObject,"handleSendException",e.toString());
		}
		finally
		{
			try
			{
				if (outputStream != null)
				{
					outputStream.close();
				}
				// Close InputStream and remove SendFileInfo from map
				try
				{
					Class btopputil= Class.forName("BluetoothOppUtility", false, loader);
					
					XposedHelpers.callStaticMethod(btopputil, "closeSendFileInfo", mInfomUri);
				}
				catch (ClassNotFoundException e)
				{
					Log.e(TAG,"ERROR BTOPPUTIL.CloseSendFileInfo()",e);
				}
				/*BluetoothOppUtility.*/;
				//getClass()
				if (!error)
				{
					responseCode = XposedHelpers.callMethod(  putOperation,"getResponseCode");
					if (responseCode != -1)
					{
						if (V) Log.v(TAG, "Get response code " + responseCode);
						if (responseCode != ResponseCodes.OBEX_HTTP_OK)
						{
							Log.i(TAG, "Response error code is " + responseCode);
							status = BluetoothShare.STATUS_UNHANDLED_OBEX_CODE;
							if (responseCode == ResponseCodes.OBEX_HTTP_UNSUPPORTED_TYPE)
							{
								status = BluetoothShare.STATUS_NOT_ACCEPTABLE;
							}
							if (responseCode == ResponseCodes.OBEX_HTTP_FORBIDDEN
								|| responseCode == ResponseCodes.OBEX_HTTP_NOT_ACCEPTABLE)
							{
								status = BluetoothShare.STATUS_FORBIDDEN;
							}
						}
					}
					else
					{
						// responseCode is -1, which means connection error
						status = BluetoothShare.STATUS_CONNECTION_ERROR;
					}
				}
				Constants.updateShareStatus(mContext1,mInfomId,status,loader);
			//	XposedHelpers.callStaticMethod(XposedHelpers.findClass("Constants",loader),"updateShareStatus",mContext1, (Object)mInfomId,(Object) status);
				if (inputStream != null)
				{
					inputStream.close();
				}
				if (putOperation != null)
				{
					XposedHelpers.callMethod( putOperation,"close");
				}
			}
			catch (IOException e)
			{
				Log.e(TAG, "Error when closing stream after send");
				// Socket has been closed due to the response timeout in the framework,
				// mark the transfer as failure.
				if (position != fileInfomLength)
				{
					status = BluetoothShare.STATUS_FORBIDDEN;
					Constants.updateShareStatus(mContext1,mInfomId,status,loader);
					//XposedHelpers.callStaticMethod(XposedHelpers.findClass("Constants",loader),"updateShareStatus",mContext1,(Object) mInfomId,(Object) status);
				}
			}
		}
		return status;
	}
	/*
	 void closeSendFileInfo(Uri uri) {
		 Class.forName(
        Log.d(TAG, "closeSendFileInfo: uri=" + uri);
		
		XposedHelpers.get
        BluetoothOppSendFileInfo info = sSendFileMap.remove(uri);
        if (info != null && info.mInputStream != null) {
            try {
                info.mInputStream.close();
            } catch (IOException ignored) {
            }
        }
		return;
    }
	*/
}

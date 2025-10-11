use jni::JNIEnv;
use jni::objects::{JClass, JString, JObject, JValue};
use jni::sys::{jboolean, jstring, jobject};

extern crate secrets_core;
use secrets_core::os;

/// Helper function to convert JString to Rust String
fn jstring_to_string(env: &mut JNIEnv, jstr: &JString) -> Result<String, String> {
    env.get_string(jstr)
        .map(|s| s.into())
        .map_err(|e| format!("Failed to convert JString: {:?}", e))
}

/// Helper function to throw Java exception
fn throw_exception(env: &mut JNIEnv, message: &str) {
    let _ = env.throw_new("java/lang/RuntimeException", message);
}

/// Set password in the system keyring
///
/// # Arguments
/// * `service` - The service name (e.g., "zowe")
/// * `account` - The account/username
/// * `password` - The password to store
///
/// # Throws
/// RuntimeException if the operation fails
#[unsafe(no_mangle)]
pub extern "system" fn Java_org_zowe_kotlinsdk_secrets_NativeSecretsLoader_setPassword(
    mut env: JNIEnv,
    _class: JClass,
    service: JString,
    account: JString,
    password: JString,
) {
    // Convert Java strings to Rust strings
    let service_str = match jstring_to_string(&mut env, &service) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return;
        }
    };

    let account_str = match jstring_to_string(&mut env, &account) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return;
        }
    };

    let password_str = match jstring_to_string(&mut env, &password) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return;
        }
    };

    // Call secrets_core to set password
    match os::set_password(&service_str, &account_str, &password_str) {
        Ok(_) => {},
        Err(e) => {
            let error_msg = format!("Failed to set password: {:?}", e);
            throw_exception(&mut env, &error_msg);
        }
    }
}

/// Get password from the system keyring
///
/// # Arguments
/// * `service` - The service name
/// * `account` - The account/username
///
/// # Returns
/// The password as String, or null if not found
///
/// # Throws
/// RuntimeException if the operation fails
#[unsafe(no_mangle)]
pub extern "system" fn Java_org_zowe_kotlinsdk_secrets_NativeSecretsLoader_getPassword<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    service: JString<'local>,
    account: JString<'local>,
) -> jstring {
    // Convert Java strings to Rust strings
    let service_str = match jstring_to_string(&mut env, &service) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return std::ptr::null_mut();
        }
    };

    let account_str = match jstring_to_string(&mut env, &account) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return std::ptr::null_mut();
        }
    };

    // Call secrets_core to get password
    match os::get_password(&service_str, &account_str) {
        Ok(Some(password)) => {
            match env.new_string(password) {
                Ok(jstr) => jstr.into_raw(),
                Err(e) => {
                    let error_msg = format!("Failed to create Java string: {:?}", e);
                    throw_exception(&mut env, &error_msg);
                    std::ptr::null_mut()
                }
            }
        }
        Ok(None) => std::ptr::null_mut(),
        Err(e) => {
            let error_msg = format!("Failed to get password: {:?}", e);
            throw_exception(&mut env, &error_msg);
            std::ptr::null_mut()
        }
    }
}

/// Delete password from the system keyring
///
/// # Arguments
/// * `service` - The service name
/// * `account` - The account/username
///
/// # Returns
/// true if the password was deleted, false otherwise
///
/// # Throws
/// RuntimeException if the operation fails
#[unsafe(no_mangle)]
pub extern "system" fn Java_org_zowe_kotlinsdk_secrets_NativeSecretsLoader_deletePassword(
    mut env: JNIEnv,
    _class: JClass,
    service: JString,
    account: JString,
) -> jboolean {
    // Convert Java strings to Rust strings
    let service_str = match jstring_to_string(&mut env, &service) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return 0;
        }
    };

    let account_str = match jstring_to_string(&mut env, &account) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return 0;
        }
    };

    // Call secrets_core to delete password
    match os::delete_password(&service_str, &account_str) {
        Ok(result) => result as jboolean,
        Err(e) => {
            let error_msg = format!("Failed to delete password: {:?}", e);
            throw_exception(&mut env, &error_msg);
            0
        }
    }
}

/// Find password by service name only
///
/// # Arguments
/// * `service` - The service name
///
/// # Returns
/// The first password found for the service, or null if not found
///
/// # Throws
/// RuntimeException if the operation fails
#[unsafe(no_mangle)]
pub extern "system" fn Java_org_zowe_kotlinsdk_secrets_NativeSecretsLoader_findPassword<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    service: JString<'local>,
) -> jstring {
    // Convert Java string to Rust string
    let service_str = match jstring_to_string(&mut env, &service) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return std::ptr::null_mut();
        }
    };

    // Call secrets_core to find password
    match os::find_password(&service_str) {
        Ok(Some(password)) => {
            match env.new_string(password) {
                Ok(jstr) => jstr.into_raw(),
                Err(e) => {
                    let error_msg = format!("Failed to create Java string: {:?}", e);
                    throw_exception(&mut env, &error_msg);
                    std::ptr::null_mut()
                }
            }
        }
        Ok(None) => std::ptr::null_mut(),
        Err(e) => {
            let error_msg = format!("Failed to find password: {:?}", e);
            throw_exception(&mut env, &error_msg);
            std::ptr::null_mut()
        }
    }
}

/// Find all credentials for a service
///
/// # Arguments
/// * `service` - The service name
///
/// # Returns
/// List<Pair<String, String>> containing (account, password) pairs
///
/// # Throws
/// RuntimeException if the operation fails
#[unsafe(no_mangle)]
pub extern "system" fn Java_org_zowe_kotlinsdk_secrets_NativeSecretsLoader_findCredentials<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    service: JString<'local>,
) -> jobject {
    // Convert Java string to Rust string
    let service_str = match jstring_to_string(&mut env, &service) {
        Ok(s) => s,
        Err(e) => {
            throw_exception(&mut env, &e);
            return JObject::null().into_raw();
        }
    };

    let mut creds: Vec<(String, String)> = vec![];

    // Call secrets_core to find credentials
    match os::find_credentials(&service_str, &mut creds) {
        Ok(_) => {
            // Create ArrayList<Pair<String, String>>
            let array_list = match env.new_object(
                "java/util/ArrayList",
                "()V",
                &[],
            ) {
                Ok(obj) => obj,
                Err(e) => {
                    let error_msg = format!("Failed to create ArrayList: {:?}", e);
                    throw_exception(&mut env, &error_msg);
                    return JObject::null().into_raw();
                }
            };

            // Add each credential as a Kotlin Pair to the list
            for (account, password) in creds {
                // Create Java strings for account and password
                let account_jstring = match env.new_string(&account) {
                    Ok(s) => s,
                    Err(e) => {
                        let error_msg = format!("Failed to create account string: {:?}", e);
                        throw_exception(&mut env, &error_msg);
                        return JObject::null().into_raw();
                    }
                };

                let password_jstring = match env.new_string(&password) {
                    Ok(s) => s,
                    Err(e) => {
                        let error_msg = format!("Failed to create password string: {:?}", e);
                        throw_exception(&mut env, &error_msg);
                        return JObject::null().into_raw();
                    }
                };

                // Create Kotlin Pair(account, password)
                let pair = match env.new_object(
                    "kotlin/Pair",
                    "(Ljava/lang/Object;Ljava/lang/Object;)V",
                    &[
                        JValue::Object(&account_jstring),
                        JValue::Object(&password_jstring),
                    ],
                ) {
                    Ok(obj) => obj,
                    Err(e) => {
                        let error_msg = format!("Failed to create Pair: {:?}", e);
                        throw_exception(&mut env, &error_msg);
                        return JObject::null().into_raw();
                    }
                };

                // Add the pair to the ArrayList
                if let Err(e) = env.call_method(
                    &array_list,
                    "add",
                    "(Ljava/lang/Object;)Z",
                    &[JValue::Object(&pair)],
                ) {
                    let error_msg = format!("Failed to add to list: {:?}", e);
                    throw_exception(&mut env, &error_msg);
                    return JObject::null().into_raw();
                }
            }

            array_list.into_raw()
        }
        Err(e) => {
            let error_msg = format!("Failed to find credentials: {:?}", e);
            throw_exception(&mut env, &error_msg);
            JObject::null().into_raw()
        }
    }
}
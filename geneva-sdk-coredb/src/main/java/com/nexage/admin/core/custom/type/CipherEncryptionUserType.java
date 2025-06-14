package com.nexage.admin.core.custom.type;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

@Log4j2
public class CipherEncryptionUserType implements UserType {

  private static final int[] SQL_TYPES = {Types.VARCHAR};

  @Override
  public int[] sqlTypes() {
    return SQL_TYPES;
  }

  @Override
  public Class<String> returnedClass() {
    return String.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == y) return true;
    if (null == x || null == y) return false;
    return x.equals(y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  @Override
  public Object nullSafeGet(
      ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    String value = rs.getString(names[0]);
    if (!rs.wasNull()) {
      try {
        SecretKey key = new SecretKeySpec(new byte[16], "AES");
        final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedByte = cipher.doFinal(Base64.decodeBase64(value));
        return new String(decryptedByte);
      } catch (Exception e) {
        log.error("Decryption process is failed", e);
        throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_CRYPTO_ERROR);
      }
    }
    return value;
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (value != null) {
      try {
        String guid = (String) value;
        SecretKey key = new SecretKeySpec(new byte[16], "AES");
        final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedByte = cipher.doFinal(guid.getBytes());
        st.setString(index, new String(Base64.encodeBase64(encryptedByte)));
      } catch (Exception e) {
        log.error("Encryption process is failed", e);
        throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_CRYPTO_ERROR);
      }
    } else {
      st.setObject(index, value);
    }
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) value;
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }
}

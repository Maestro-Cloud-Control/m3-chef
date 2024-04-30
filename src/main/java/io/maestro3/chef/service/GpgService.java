/*
 * Copyright 2023 Maestro Cloud Control LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.maestro3.chef.service;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;


@Service
public class GpgService implements IGpgService {

    private static final Logger LOG = LoggerFactory.getLogger(GpgService.class);

    private String keyId;

    @Autowired
    public GpgService(@Value("${gnupg.user:epamorchconfig}") String keyId) {
        this.keyId = keyId;
    }

    @Override
    public String encrypt(String value) {
        try {
            return new String(encrypt(keyId.getBytes(), value.toCharArray(), "iway", SymmetricKeyAlgorithmTags.CAST5, true));
        } catch (Exception e) {
            LOG.error("Problem during GPG encryption: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Simple PGP encryptor between byte[].
     *
     * @param clearData  The data to be encrypted
     * @param passPhrase The pass phrase (key).  This method assumes that the
     *                   key is a simple pass phrase, and does not yet support
     *                   RSA or more sophisiticated keying.
     * @param fileName   File name. This is used in the Literal Data Packet (tag 11)
     *                   which is really inly important if the data is to be
     *                   related to a file to be recovered later.  Because this
     *                   routine does not know the source of the information, the
     *                   caller can set something here for file name use that
     *                   will be carried.  If this routine is being used to
     *                   encrypt SOAP MIME bodies, for example, use the file name from the
     *                   MIME type, if applicable. Or anything else appropriate.
     * @param armor
     * @return encrypted data.
     * @throws IOException
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    public static byte[] encrypt(
            byte[] clearData,
            char[] passPhrase,
            String fileName,
            int algorithm,
            boolean armor)
            throws IOException, PGPException {
        if (fileName == null) {
            fileName = PGPLiteralData.CONSOLE;
        }

        byte[] compressedData = compress(clearData, fileName, CompressionAlgorithmTags.ZIP);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        OutputStream out = bOut;
        if (armor) {
            out = new ArmoredOutputStream(out);
        }

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(algorithm).setSecureRandom(new SecureRandom()).setProvider("BC"));
        encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator(passPhrase).setProvider("BC"));


        try (OutputStream encOut = encGen.open(out, compressedData.length)) {
            encOut.write(compressedData);
        }

        if (armor) {
            out.close();
        }

        return bOut.toByteArray();
    }


    private static byte[] compress(byte[] clearData, String fileName, int algorithm) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
        OutputStream cos = comData.open(bOut); // open it with the final destination

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        // we want to generate compressed data. This might be a user option later,
        // in which case we would pass in bOut.

        try (OutputStream pOut = lData.open(cos, // the compressed output stream
            PGPLiteralData.BINARY,
            fileName,  // "filename" to store
            clearData.length, // length of clear data
            new Date()  // current time
        )) {
            pOut.write(clearData);
        }
        comData.close();

        return bOut.toByteArray();
    }
}

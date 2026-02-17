package com.arch.data.remote.retrofit.config

import android.os.Build
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Configuration utility for SSL/TLS certificate handling.
 * Provides methods for handling SSL configurations including Android 7 (API 24) support.
 *
 * IMPORTANT: The permissive trust manager should ONLY be used in development/testing.
 * DO NOT use in production as it defeats the purpose of HTTPS security.
 */
object SslConfig {
    private const val TLS_VERSION = "TLSv1.2"

    /**
     * Create a custom TrustManager that accepts all certificates.
     * WARNING: Only use for development/testing with self-signed certificates.
     * DO NOT use in production - this disables certificate validation.
     */
    @Suppress("CustomX509TrustManager", "TrustAllX509TrustManager")
    fun createPermissiveTrustManager(): X509TrustManager =
        object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

            override fun checkClientTrusted(
                certs: Array<X509Certificate>,
                authType: String
            ) {
                // Intentionally empty - accepts all client certificates
            }

            override fun checkServerTrusted(
                certs: Array<X509Certificate>,
                authType: String
            ) {
                // Intentionally empty - accepts all server certificates
            }
        }

    /**
     * Create an SSL context with custom trust manager.
     * Uses TLS 1.2 for compatibility with Android 7+ devices.
     * @param trustManager Custom trust manager, or null to use system default
     */
    fun createSslContext(trustManager: X509TrustManager? = null): SSLContext {
        val sslContext = SSLContext.getInstance(TLS_VERSION)
        val trustManagers = trustManager?.let { arrayOf(it as javax.net.ssl.TrustManager) }
        sslContext.init(null, trustManagers, SecureRandom())
        return sslContext
    }

    /**
     * Create an SSL socket factory with TLS 1.2 enabled.
     * This is specifically for Android 7 (API 24) devices where TLS 1.2
     * may not be enabled by default.
     * @param trustManager Custom trust manager for certificate handling
     */
    fun createTls12SocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        val sslContext = createSslContext(trustManager)
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Tls12SocketFactory(sslContext.socketFactory)
        } else {
            sslContext.socketFactory
        }
    }

    /**
     * Wrapper SSLSocketFactory that enables TLS 1.2 on older Android versions.
     * Android 7.0 (API 24) supports TLS 1.2 but may not enable it by default.
     */
    private class Tls12SocketFactory(
        private val delegate: SSLSocketFactory
    ) : SSLSocketFactory() {
        private val tlsProtocols = arrayOf("TLSv1.2", "TLSv1.1")

        override fun getDefaultCipherSuites(): Array<String> = delegate.defaultCipherSuites

        override fun getSupportedCipherSuites(): Array<String> = delegate.supportedCipherSuites

        override fun createSocket(
            s: java.net.Socket?,
            host: String?,
            port: Int,
            autoClose: Boolean
        ): java.net.Socket = enableTls(delegate.createSocket(s, host, port, autoClose))

        override fun createSocket(
            host: String?,
            port: Int
        ): java.net.Socket = enableTls(delegate.createSocket(host, port))

        override fun createSocket(
            host: String?,
            port: Int,
            localHost: java.net.InetAddress?,
            localPort: Int
        ): java.net.Socket = enableTls(delegate.createSocket(host, port, localHost, localPort))

        override fun createSocket(
            host: java.net.InetAddress?,
            port: Int
        ): java.net.Socket = enableTls(delegate.createSocket(host, port))

        override fun createSocket(
            address: java.net.InetAddress?,
            port: Int,
            localAddress: java.net.InetAddress?,
            localPort: Int
        ): java.net.Socket = enableTls(delegate.createSocket(address, port, localAddress, localPort))

        private fun enableTls(socket: java.net.Socket): java.net.Socket {
            if (socket is javax.net.ssl.SSLSocket) {
                socket.enabledProtocols = tlsProtocols
            }
            return socket
        }
    }
}

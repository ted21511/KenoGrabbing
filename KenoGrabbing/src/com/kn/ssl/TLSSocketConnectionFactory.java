package com.kn.ssl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.ExtensionType;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsCredentials;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class TLSSocketConnectionFactory extends SSLSocketFactory {

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Adding Custom BouncyCastleProvider
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
			Security.addProvider(new BouncyCastleProvider());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// HANDSHAKE LISTENER
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class TLSHandshakeListener implements HandshakeCompletedListener {

		public void handshakeCompleted(HandshakeCompletedEvent event) {

		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SECURE RANDOM
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private SecureRandom _secureRandom = new SecureRandom();

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Adding Custom BouncyCastleProvider
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Socket createSocket(Socket socket, final String host, int port, boolean arg3) throws IOException {
		if (socket == null) {
			socket = new Socket();
		}
		if (!socket.isConnected()) {
			socket.connect(new InetSocketAddress(host, port));
		}

		final TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(socket.getInputStream(),
				socket.getOutputStream(), _secureRandom);
		return _createSSLSocket(host, tlsClientProtocol);

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SOCKET FACTORY METHODS
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public String[] getDefaultCipherSuites() {
		return null;
	}

	public String[] getSupportedCipherSuites() {
		return null;
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return null;
	}

	public Socket createSocket(InetAddress host, int port) throws IOException {
		return null;
	}

	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return null;
	}

	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
			throws IOException {
		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// SOCKET CREATION
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private SSLSocket _createSSLSocket(final String host, final TlsClientProtocol tlsClientProtocol) {
		return new SSLSocket() {
			private java.security.cert.Certificate[] peertCerts;

			public InputStream getInputStream() throws IOException {
				return tlsClientProtocol.getInputStream();
			}

			public OutputStream getOutputStream() throws IOException {
				return tlsClientProtocol.getOutputStream();
			}

			public synchronized void close() throws IOException {
				tlsClientProtocol.close();
			}

			public void addHandshakeCompletedListener(HandshakeCompletedListener arg0) {

			}

			public boolean getEnableSessionCreation() {
				return false;
			}

			public String[] getEnabledCipherSuites() {
				return null;
			}

			public String[] getEnabledProtocols() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean getNeedClientAuth() {
				return false;
			}

			public SSLSession getSession() {
				return new SSLSession() {

					public int getApplicationBufferSize() {
						return 0;
					}

					public String getCipherSuite() {
						throw new UnsupportedOperationException();
					}

					public long getCreationTime() {
						throw new UnsupportedOperationException();
					}

					public byte[] getId() {
						throw new UnsupportedOperationException();
					}

					public long getLastAccessedTime() {
						throw new UnsupportedOperationException();
					}

					public java.security.cert.Certificate[] getLocalCertificates() {
						throw new UnsupportedOperationException();
					}

					public Principal getLocalPrincipal() {
						throw new UnsupportedOperationException();
					}

					public int getPacketBufferSize() {
						throw new UnsupportedOperationException();
					}

					public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
						// TODO Auto-generated method stub
						return null;
					}

					public java.security.cert.Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
						return peertCerts;
					}

					public String getPeerHost() {
						throw new UnsupportedOperationException();
					}

					public int getPeerPort() {
						return 0;
					}

					public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
						return null;
						// throw new UnsupportedOperationException();

					}

					public String getProtocol() {
						throw new UnsupportedOperationException();
					}

					public SSLSessionContext getSessionContext() {
						throw new UnsupportedOperationException();
					}

					public Object getValue(String arg0) {
						throw new UnsupportedOperationException();
					}

					public String[] getValueNames() {
						throw new UnsupportedOperationException();
					}

					public void invalidate() {
						throw new UnsupportedOperationException();

					}

					public boolean isValid() {
						throw new UnsupportedOperationException();
					}

					public void putValue(String arg0, Object arg1) {
						throw new UnsupportedOperationException();

					}

					public void removeValue(String arg0) {
						throw new UnsupportedOperationException();

					}

				};
			}

			public String[] getSupportedProtocols() {
				return null;
			}

			public boolean getUseClientMode() {
				return false;
			}

			public boolean getWantClientAuth() {

				return false;
			}

			public void removeHandshakeCompletedListener(HandshakeCompletedListener arg0) {

			}

			public void setEnableSessionCreation(boolean arg0) {

			}

			public void setEnabledCipherSuites(String[] arg0) {

			}

			public void setEnabledProtocols(String[] arg0) {

			}

			public void setNeedClientAuth(boolean arg0) {

			}

			public void setUseClientMode(boolean arg0) {

			}

			public void setWantClientAuth(boolean arg0) {

			}

			public String[] getSupportedCipherSuites() {
				return null;
			}

			public void startHandshake() throws IOException {
				tlsClientProtocol.connect(new DefaultTlsClient() {

					public Hashtable<Integer, byte[]> getClientExtensions() throws IOException {
						Hashtable<Integer, byte[]> clientExtensions = super.getClientExtensions();
						if (clientExtensions == null) {
							clientExtensions = new Hashtable<Integer, byte[]>();
						}

						// Add host_name
						byte[] host_name = host.getBytes();

						final ByteArrayOutputStream baos = new ByteArrayOutputStream();
						final DataOutputStream dos = new DataOutputStream(baos);
						dos.writeShort(host_name.length + 3); // entry size
						dos.writeByte(0); // name type = hostname
						dos.writeShort(host_name.length);
						dos.write(host_name);
						dos.close();
						clientExtensions.put(ExtensionType.server_name, baos.toByteArray());
						return clientExtensions;
					}

					public TlsAuthentication getAuthentication() throws IOException {
						return new TlsAuthentication() {

							public void notifyServerCertificate(Certificate serverCertificate) throws IOException {

								try {
									CertificateFactory cf = CertificateFactory.getInstance("X.509");
									List<java.security.cert.Certificate> certs = new LinkedList<java.security.cert.Certificate>();
									for (org.bouncycastle.asn1.x509.Certificate c : serverCertificate
											.getCertificateList()) {
										certs.add(cf.generateCertificate(new ByteArrayInputStream(c.getEncoded())));
									}
									peertCerts = certs.toArray(new java.security.cert.Certificate[0]);
								} catch (CertificateException e) {
									System.out.println("Failed to cache server certs" + e);
									throw new IOException(e);
								}

							}

							public TlsCredentials getClientCredentials(CertificateRequest arg0) throws IOException {
								return null;
							}

						};

					}

				});

			}

		};// Socket

	}
}

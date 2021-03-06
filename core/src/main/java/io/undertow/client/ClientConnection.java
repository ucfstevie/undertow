package io.undertow.client;

import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;

/**
 * A client connection. This can be used to send requests, or to upgrade the connection.
 * <p/>
 * In general these objects are not thread safe, they should only be used by the IO thread
 * that is responsible for the connection. As a result this client does not provide a mechanism
 * to perform blocking IO, it is designed for async operation only.
 *
 * @author Stuart Douglas
 */
public interface ClientConnection extends Channel {

    /**
     * Sends a client request. The request object should not be modified after it has been submitted to the connection.
     * <p/>
     * Request objects can be queued. Once the request is in a state that it is ready to be sent the {@code clientCallback}
     * is invoked to provide the caller with the {@link ClientExchange}
     * <p/>
     * Note that the request header may not be written out until after the callback has been invoked. This allows the
     * client to write out a header with a gathering write if the request contains content.
     *
     * @param request The request to send.
     * @return The resulting client exchange, that can be used to send the request body and read the response
     */
    void sendRequest(final ClientRequest request, final ClientCallback<ClientExchange> clientCallback);

    /**
     * Upgrade the connection, if the underlying protocol supports it. This should only be called after an upgrade request
     * has been submitted and the target server has accepted the upgrade.
     *
     * @return The resulting StreamConnection
     */
    StreamConnection performUpgrade() throws IOException;

    Pool<ByteBuffer> getBufferPool();

    SocketAddress getPeerAddress();

    <A extends SocketAddress> A getPeerAddress(Class<A> type);

    ChannelListener.Setter<? extends ClientConnection> getCloseSetter();

    SocketAddress getLocalAddress();

    <A extends SocketAddress> A getLocalAddress(Class<A> type);

    XnioWorker getWorker();

    XnioIoThread getIoThread();

    boolean isOpen();

    boolean supportsOption(Option<?> option);

    <T> T getOption(Option<T> option) throws IOException;

    <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException;

    boolean isUpgraded();
}

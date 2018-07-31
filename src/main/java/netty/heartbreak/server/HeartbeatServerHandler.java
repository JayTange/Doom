package netty.heartbreak.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Random;

/**
 * @author tangj
 * @date 2018/6/10 12:21
 */
public class HeartbeatServerHandler extends SimpleChannelInboundHandler<String> {
    // ʧ�ܼ�������δ�յ�client�˷��͵�ping����
    private int unRecPingTimes = 0;

    // ��������û���յ�������Ϣ��������
    private static final int MAX_UN_REC_PING_TIMES = 3;

    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg!=null && msg.equals("Heartbeat")){
            System.out.println("�ͻ���"+ctx.channel().remoteAddress()+"--������Ϣ--");
        }else {
            System.out.println("�ͻ���----������Ϣ----��"+msg);
            String resp = "��Ʒ�ļ۸��ǣ�"+random.nextInt(1000);
            ctx.writeAndFlush(resp);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state()== IdleState.READER_IDLE){
                System.out.println("===�����===(READER_IDLE ����ʱ)");
                // ʧ�ܼ������������ڵ���3�ε�ʱ�򣬹ر����ӣ��ȴ�client����
                if (unRecPingTimes >= MAX_UN_REC_PING_TIMES) {
                    System.out.println("===�����===(����ʱ���ر�chanel)");
                    // ��������N��δ�յ�client��ping��Ϣ����ô�رո�ͨ�����ȴ�client����
                    ctx.close();
                } else {
                    // ʧ�ܼ�������1
                    unRecPingTimes++;
                }
            }else {
                super.userEventTriggered(ctx,evt);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("һ���ͻ���������");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("һ���ͻ����ѶϿ�����");
    }
}

package com.ruoyi;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.core.env.Environment;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RuoYiApplication
{
    public static void main(String[] args)
    {
        // 加载 .env 文件
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()    // 如果文件不存在也不报错
                    .load();

            System.out.println(".env 文件加载成功！");

            // 将环境变量设置到系统属性中，供 Spring 配置文件使用
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
                System.out.println("加载变量: " + entry.getKey() + " = " + entry.getValue());
            });

            // 验证系统属性是否设置成功
            System.out.println("验证 - TOKEN_SECRET: " + System.getProperty("TOKEN_SECRET"));
        } catch (DotenvException e) {
            System.out.println("警告: .env 文件加载失败，将使用默认配置: " + e.getMessage());
            e.printStackTrace();
        }

        // 启动应用
        Environment env = SpringApplication.run(RuoYiApplication.class, args).getEnvironment();

        // 验证环境变量是否正确读取（测试用，确认后可删除）
        System.out.println("=== 环境变量检查 ===");
        System.out.println("DB_URL: " + System.getProperty("DB_URL"));
        System.out.println("DB_USERNAME: " + System.getProperty("DB_USERNAME"));
        System.out.println("REDIS_HOST: " + System.getProperty("REDIS_HOST"));
        System.out.println("token.secret: " + env.getProperty("token.secret"));
        System.out.println("===================");
        System.out.println("(♥◠‿◠)ﾉﾞ  若依启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
